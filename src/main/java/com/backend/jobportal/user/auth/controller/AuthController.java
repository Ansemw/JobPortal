package com.backend.jobportal.user.auth.controller;

import com.backend.jobportal.constants.ApplicationConstant;
import com.backend.jobportal.entity.JobPortalUser;
import com.backend.jobportal.user.auth.dto.LoginRequestDto;
import com.backend.jobportal.user.auth.dto.LoginResponseDto;
import com.backend.jobportal.user.auth.dto.RegisterRequestDto;
import com.backend.jobportal.user.auth.service.IAuthService;
import com.backend.jobportal.security.util.JwtUtil;
import com.backend.jobportal.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final IAuthService authService;

    @PostMapping(value = "/login/public", version = "1.0")
    public ResponseEntity<LoginResponseDto> apiLogin(@RequestBody LoginRequestDto loginRequestDto) {

        try {
            var resultAuthentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.userName()
                    ,loginRequestDto.password()));

            String jwt = jwtUtil.generateJwtToken(resultAuthentication);
            UserDto userDto = new UserDto();
            var loggedInUser = (JobPortalUser)resultAuthentication.getPrincipal();
            BeanUtils.copyProperties(loggedInUser, userDto);
            userDto.setRole(loggedInUser.getRole().getName());
            userDto.setUserId(loggedInUser.getId());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new LoginResponseDto("Logged in", userDto, jwt ));

        }catch (BadCredentialsException e) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }catch (AuthenticationException e) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication Failed");
        }catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }

    }

    @PostMapping(value = "/register/public", version = "1.0")
    public ResponseEntity<?> apiRegister(@RequestBody RegisterRequestDto registerRequestDto) {

        Map<String, String> response= authService.apiRegister(registerRequestDto);
       if(response.get(ApplicationConstant.STATUS_SUCCESS)!=null) {
           return ResponseEntity.status(HttpStatus.CREATED).body(response);
       }
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }

    private ResponseEntity<LoginResponseDto> buildErrorResponse( HttpStatus status,String message) {

        return ResponseEntity.status(status).body(new LoginResponseDto(message, null, null));
    }
}


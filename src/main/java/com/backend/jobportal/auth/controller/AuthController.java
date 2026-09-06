package com.backend.jobportal.auth.controller;

import com.backend.jobportal.auth.dto.LoginRequestDto;
import com.backend.jobportal.auth.dto.LoginResponseDto;
import com.backend.jobportal.security.util.JwtUtil;
import com.backend.jobportal.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @PostMapping(value = "/login/public", version = "1.0")
    public ResponseEntity<LoginResponseDto> apiLogin(@RequestBody LoginRequestDto loginRequestDto) {

        try {
            var resultAuthentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.userName()
                    ,loginRequestDto.password()));

            return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDto(
                    "Logged in", new UserDto(), jwtUtil.generateJwtToken(resultAuthentication)
            ));
        }catch (BadCredentialsException e) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }catch (AuthenticationException e) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication Failed");
        }catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }

    }

    private ResponseEntity<LoginResponseDto> buildErrorResponse( HttpStatus status,String message) {

        return ResponseEntity.status(status).body(new LoginResponseDto(message, null, null));
    }
}


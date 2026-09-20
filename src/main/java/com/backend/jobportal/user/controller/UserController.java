package com.backend.jobportal.user.controller;

import com.backend.jobportal.user.dto.UserDto;
import com.backend.jobportal.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    // Handles GET requests to look up a user by email.
    // Returns 200 with the user if found, 404 if no user exists with that email.
    @GetMapping(path = "/search/admin", version = "1.0")
    public ResponseEntity<?> findUserByEmail(@RequestParam String email) {
        Optional<UserDto> userDto = userService.findUserByEmail(email);
        if (userDto.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(userDto.get());
        }
        else   return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "User not found with email: " + email));
    }

    // Handles PATCH requests to elevate a user's role to ROLE_EMPLOYER.
    // Returns 200 with the updated user if found, 404 if no user exists with that id.
    @PatchMapping(path = "{userId}/employer/role/admin", version = "1.0")
    public ResponseEntity<?> elevateUserToEmployerRole(@PathVariable Long userId) {
        Optional<UserDto> userDto = userService.elevateUserToEmployer(userId);

        if (userDto.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(userDto.get());
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(userId, "Not Found"));
        }

    }

    // Handles PATCH requests to associate an employer user with a company.
    // Returns 200 with the updated user if found, 404 if no user exists with that id.
    @PatchMapping(path = "{userId}/company/{companyId}/admin", version = "1.0")
    public ResponseEntity<UserDto> assignCompanyToUser(@PathVariable Long userId, @PathVariable Long companyId) {
        Optional<UserDto> userDto = userService.assignCompanyToUser(userId, companyId);
        return userDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}

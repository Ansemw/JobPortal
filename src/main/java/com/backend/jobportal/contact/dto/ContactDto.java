package com.backend.jobportal.contact.dto;

import jakarta.validation.constraints.*;

import java.time.Instant;

public record ContactDto(

        @NotBlank(message = "Email can't be empty")
        @Email(message = "Send valid email")
        String email,

        @NotBlank(message = "Message can't be empty")
        @Size(min = 5, max = 500 , message = "Message must be between 5 and 500 characters")
        String message,

        @NotBlank(message = "Name can't be empty")
        @Size(min = 5, max = 30 , message = "Message must be between 5 and 30 characters")
        String name,

        @NotBlank(message = "Subject can't be empty")
        @Size(min = 5, max = 150 , message = "Message must be between 5 and 150 characters")
        String subject,

        @NotBlank(message = "User Type can't be empty")
        @Pattern(regexp = "jobseeker|employer|other")
        String userType
) {
}

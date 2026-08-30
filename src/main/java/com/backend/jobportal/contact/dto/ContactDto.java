package com.backend.jobportal.contact.dto;

import java.time.Instant;

public record ContactDto(

        String email,
        String message,
        String name,
        String subject,
        String userType
) {
}

package com.backend.jobportal.example.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

public record ExampleDto(
        Long id,

        // Flattened @ManyToOne: the id plus the few display fields the UI needs
        // (populated server-side from the caller's company, never trusted from the request body).
        Long companyId,
        String companyName,

        @NotBlank(message = "Name can't be empty")
        @Size(max = 255, message = "Name can't exceed 255 characters")
        String name,

        @Size(max = 500, message = "Website can't exceed 500 characters")
        String website,

        @DecimalMin(value = "0.0", message = "Rating can't be negative")
        @DecimalMax(value = "5.0", message = "Rating can't exceed 5.0")
        BigDecimal rating,

        String description,

        Instant deadline,

        // Must match the option list the UI actually submits — keep both in sync.
        @Pattern(regexp = "^(ACTIVE|CLOSED|DRAFT)$", message = "Status must be one of ACTIVE, CLOSED, DRAFT")
        String status,

        Instant createdAt
) {
}

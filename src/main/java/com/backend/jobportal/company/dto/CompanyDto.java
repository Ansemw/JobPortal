package com.backend.jobportal.company.dto;

import com.backend.jobportal.entity.Job;
import com.backend.jobportal.job.dto.JobDto;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CompanyDto(
        Long id,

        @NotBlank(message = "Name can't be empty")
        @Size(max = 255, message = "Name can't exceed 255 characters")
        String name,

        @Size(max = 500, message = "Logo URL can't exceed 500 characters")
        String logo,

        @NotBlank(message = "Industry can't be empty")
        @Size(max = 100, message = "Industry can't exceed 100 characters")
        String industry,

        @NotBlank(message = "Size can't be empty")
        @Size(max = 50, message = "Size can't exceed 50 characters")
        String size,

        @NotNull(message = "Rating can't be empty")
        @DecimalMin(value = "0.0", message = "Rating can't be less than 0.0")
        @DecimalMax(value = "5.0", message = "Rating can't be more than 5.0")
        @Digits(integer = 1, fraction = 2, message = "Rating must have at most 1 integer digit and 2 decimal digits")
        BigDecimal rating,

        @Size(max = 1000, message = "Locations can't exceed 1000 characters")
        String locations,

        @NotNull(message = "Founded year can't be empty")
        @Min(value = 1800, message = "Founded year must be 1800 or later")
        Integer founded,

        String description,

        @Min(value = 0, message = "Employees can't be negative")
        Integer employees,

        @Pattern(regexp = "^(https?://)[\\w.-]+(\\.[a-zA-Z]{2,})(/\\S*)?$", message = "Website must be a valid URL")
        String website,

        Instant createdAt,
        List<JobDto> jobs
) {
}

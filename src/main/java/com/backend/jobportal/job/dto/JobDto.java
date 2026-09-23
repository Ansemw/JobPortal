package com.backend.jobportal.job.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

public record JobDto(
        Long id,

        @NotBlank(message = "Title can't be empty")
        @Size(max = 255, message = "Title can't exceed 255 characters")
        String title,

        Long companyId,
        String companyName,
        String companyLogo,

        @NotBlank(message = "Location can't be empty")
        String location,

        @NotBlank(message = "Work type can't be empty")
        @Pattern(regexp = "^(Remote|Hybrid|On-site)$", message = "Work type must be one of Remote, Hybrid, On-site")
        String workType,

        @NotBlank(message = "Job type can't be empty")
        @Pattern(regexp = "^(Full-time|Part-time|Contract|Internship)$", message = "Job type must be one of Full-time, Part-time, Contract, Internship")
        String jobType,

        @NotBlank(message = "Category can't be empty")
        @Pattern(regexp = "^(Technology|Marketing|Sales|Design|Finance|Operations|Other)$", message = "Category must be one of Technology, Marketing, Sales, Design, Finance, Operations, Other")
        String category,

        @NotBlank(message = "Experience level can't be empty")
        @Pattern(regexp = "^(Entry-level|Mid-level|Senior-level|Executive)$", message = "Experience level must be one of Entry-level, Mid-level, Senior-level, Executive")
        String experienceLevel,

        @NotNull(message = "Minimum salary can't be empty")
        @DecimalMin(value = "0.0", message = "Minimum salary can't be negative")
        BigDecimal salaryMin,

        @NotNull(message = "Maximum salary can't be empty")
        @DecimalMin(value = "0.0", message = "Maximum salary can't be negative")
        BigDecimal salaryMax,

        @NotBlank(message = "Salary currency can't be empty")
        @Pattern(regexp = "^(USD|EUR|GBP|CAD|AUD|INR)$", message = "Salary currency must be one of USD, EUR, GBP, CAD, AUD, INR")
        String salaryCurrency,

        @NotBlank(message = "Salary period can't be empty")
        @Pattern(regexp = "^(year|month|hour)$", message = "Salary period must be one of year, month, hour")
        String salaryPeriod,

        @NotBlank(message = "Description can't be empty")
        String description,

        String requirements,
        String benefits,

        Instant postedDate,
        Instant applicationDeadline,

        @Min(value = 0, message = "Applications count can't be negative")
        Integer applicationsCount,

        Boolean featured,
        Boolean urgent,
        Boolean remote,

        @Pattern(regexp = "^(ACTIVE|CLOSED|DRAFT)$", message = "Status must be one of ACTIVE, CLOSED, DRAFT")
        String status

) {
}

package com.backend.jobportal.jobactions.dto;

import com.backend.jobportal.job.dto.JobDto;
import com.backend.jobportal.user.profile.dto.ProfileDto;

import java.time.Instant;

public record JobApplicationDto(
        Long id,
        Long userId,
        String userName,
        String userEmail,
        String userMobileNumber,
        ProfileDto userProfile,
        JobDto job,
        Instant appliedAt,
        String status,
        String coverLetter,
        String notes
) {
}

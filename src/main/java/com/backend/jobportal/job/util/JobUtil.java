package com.backend.jobportal.job.util;

import com.backend.jobportal.entity.Job;
import com.backend.jobportal.job.dto.JobDto;

public final class JobUtil {

    private JobUtil() {
    }

    // Copies the fields of a Job entity into a new JobDto. company id/name/logo are null-safe.
    public static JobDto transformJobToDto(Job job) {
        return new JobDto(
                job.getId(),
                job.getTitle(),
                job.getCompany() != null ? job.getCompany().getId() : null,
                job.getCompany() != null ? job.getCompany().getName() : null,
                job.getCompany() != null ? job.getCompany().getLogo() : null,
                job.getLocation(),
                job.getWorkType(),
                job.getJobType(),
                job.getCategory(),
                job.getExperienceLevel(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getSalaryCurrency(),
                job.getSalaryPeriod(),
                job.getDescription(),
                job.getRequirements(),
                job.getBenefits(),
                job.getPostedDate(),
                job.getApplicationDeadline(),
                job.getApplicationsCount(),
                job.getFeatured(),
                job.getUrgent(),
                job.getRemote(),
                job.getStatus()
        );
    }

    // Copies the writable fields of a JobDto into a brand-new Job entity. company/postedDate
    // are set separately by the caller; id/createdAt/updatedAt/createdBy/updatedBy are
    // DB-generated/audit-managed.
    public static Job transformDtoToJob(JobDto jobDto) {
        Job job = new Job();
        job.setTitle(jobDto.title());
        job.setLocation(jobDto.location());
        job.setWorkType(jobDto.workType());
        job.setJobType(jobDto.jobType());
        job.setCategory(jobDto.category());
        job.setExperienceLevel(jobDto.experienceLevel());
        job.setSalaryMin(jobDto.salaryMin());
        job.setSalaryMax(jobDto.salaryMax());
        job.setSalaryCurrency(jobDto.salaryCurrency());
        job.setSalaryPeriod(jobDto.salaryPeriod());
        job.setDescription(jobDto.description());
        job.setRequirements(jobDto.requirements());
        job.setBenefits(jobDto.benefits());
        job.setApplicationDeadline(jobDto.applicationDeadline());
        job.setApplicationsCount(jobDto.applicationsCount());
        job.setFeatured(jobDto.featured());
        job.setUrgent(jobDto.urgent());
        job.setRemote(jobDto.remote());
        job.setStatus(jobDto.status());
        return job;
    }
}

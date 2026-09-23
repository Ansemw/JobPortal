package com.backend.jobportal.job.service.impl;

import com.backend.jobportal.entity.Company;
import com.backend.jobportal.entity.Job;
import com.backend.jobportal.entity.JobPortalUser;
import com.backend.jobportal.job.dto.JobDto;
import com.backend.jobportal.job.repository.JobRepository;
import com.backend.jobportal.job.service.IJobService;
import com.backend.jobportal.user.repository.JobPortalUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JobServiceImpl implements IJobService {

    private static final Set<String> VALID_STATUSES = Set.of("ACTIVE", "CLOSED", "DRAFT");

    private final JobRepository jobRepository;
    private final JobPortalUserRepository jobPortalUserRepository;


    @Override
    public List<JobDto> getAllJobs() {
        List<Job> jobs = jobRepository.findAll();
        List<JobDto> dto = jobs.stream().map(this::transformToDto).toList();
        return dto;
    }

    @Override
    public List<JobDto> getEmployerJob(String email) {
        JobPortalUser employer = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        Company company = employer.getCompany();
        if (company == null) {
            throw new RuntimeException("Employer is not associated with any company");
        }

        List<Job> jobs = jobRepository.findByCompanyId(company.getId());
        return jobs.stream().map(this::transformToDto).toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "companiesPublic", allEntries = true)
    public JobDto updateJobStatus(Long jobId, String status, String email) {
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw new RuntimeException("Status must be one of ACTIVE, CLOSED, DRAFT");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        JobPortalUser employer = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        if (employer.getCompany() == null || job.getCompany() == null
                || !job.getCompany().getId().equals(employer.getCompany().getId())) {
            throw new RuntimeException("You are not authorized to update this job");
        }

        int rowsAffected = jobRepository.updateStatusById(jobId, status, email);
        if (rowsAffected == 0) {
            throw new RuntimeException("Job not found");
        }

        Job updatedJob = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return transformToDto(updatedJob);
    }

    @Override
    @Transactional
    @CacheEvict(value = "companiesPublic", allEntries = true)
    public JobDto createJob(JobDto jobDto, String email) {
        JobPortalUser employer = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        Company company = employer.getCompany();
        if (company == null) {
            throw new RuntimeException("You must be associated with a company to post a job");
        }

        Job job = transformDtoToJob(jobDto);
        job.setCompany(company);
        job.setPostedDate(Instant.now());
        job.setApplicationsCount(0);
        job.setStatus("DRAFT");
        Job savedJob = jobRepository.save(job);
        return transformToDto(savedJob);
    }

    // Copies the writable fields of a JobDto into a brand-new Job entity. company/postedDate
    // are set separately by createJob; id/createdAt/updatedAt/createdBy/updatedBy are
    // DB-generated/audit-managed.
    private Job transformDtoToJob(JobDto jobDto) {
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

    private JobDto transformToDto(Job job) {
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
}

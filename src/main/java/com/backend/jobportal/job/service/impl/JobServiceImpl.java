package com.backend.jobportal.job.service.impl;

import com.backend.jobportal.entity.Company;
import com.backend.jobportal.entity.Job;
import com.backend.jobportal.entity.JobPortalUser;
import com.backend.jobportal.job.dto.JobDto;
import com.backend.jobportal.job.repository.JobRepository;
import com.backend.jobportal.job.service.IJobService;
import com.backend.jobportal.job.util.JobUtil;
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
        List<JobDto> dto = jobs.stream().map(JobUtil::transformJobToDto).toList();
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
        return jobs.stream().map(JobUtil::transformJobToDto).toList();
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
        return JobUtil.transformJobToDto(updatedJob);
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

        Job job = JobUtil.transformDtoToJob(jobDto);
        job.setCompany(company);
        job.setPostedDate(Instant.now());
        job.setApplicationsCount(0);
        job.setStatus("DRAFT");
        Job savedJob = jobRepository.save(job);
        return JobUtil.transformJobToDto(savedJob);
    }
}

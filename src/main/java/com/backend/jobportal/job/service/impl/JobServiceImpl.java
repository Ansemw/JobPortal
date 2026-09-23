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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JobServiceImpl implements IJobService {

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

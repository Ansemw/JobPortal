package com.backend.jobportal.company.service.impl;

import com.backend.jobportal.company.dto.CompanyDto;
import com.backend.jobportal.company.repository.CompanyRepository;
import com.backend.jobportal.company.service.ICompanyService;
import com.backend.jobportal.constants.ApplicationConstant;
import com.backend.jobportal.entity.Company;
import com.backend.jobportal.entity.Job;
import com.backend.jobportal.job.dto.JobDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements ICompanyService {


    private final CompanyRepository companyRepository;

    // Injects the repository used to talk to the companies table.
    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // Fetches every company from the database and converts each one into a CompanyDto.
    @Override
    public List<CompanyDto> getAllCompanies() {

        List<Company> companies= companyRepository.findAllByJobStatus(ApplicationConstant.STATUS_ACTIVE);
        List<CompanyDto> dto=companies.stream().map(this::transformCompanyToDto).toList();
        return dto;
    }

    // Copies the fields of a Company entity into a new CompanyDto.
    private CompanyDto transformCompanyToDto(Company company) {
        return new CompanyDto(
                company.getId(),
                company.getName(),
                company.getLogo(),
                company.getIndustry(),
                company.getSize(),
                company.getRating(),
                company.getLocations(),
                company.getFounded(),
                company.getDescription(),
                company.getEmployees(),
                company.getWebsite(),
                company.getCreatedAt(),
                company.getJobs().stream().map(job -> transformJobToDto(job)).collect(Collectors.toList())
        );
    }

    private JobDto transformJobToDto(Job job) {
        return new JobDto(
                job.getId(),
                job.getTitle(),
                job.getCompany().getId(),
                job.getCompany().getName(),
                job.getCompany().getLogo(),
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

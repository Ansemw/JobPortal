package com.backend.jobportal.company.service.impl;

import com.backend.jobportal.company.dto.CompanyDto;
import com.backend.jobportal.company.repository.CompanyRepository;
import com.backend.jobportal.company.service.ICompanyService;
import com.backend.jobportal.constants.ApplicationConstant;
import com.backend.jobportal.entity.Company;
import com.backend.jobportal.entity.Job;
import com.backend.jobportal.job.dto.JobDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CompanyServiceImpl implements ICompanyService {


    private final CompanyRepository companyRepository;

    // Injects the repository used to talk to the companies table.
    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // Fetches every company from the database and converts each one into a CompanyDto.
    @Override
    @Cacheable("companiesPublic")
    public List<CompanyDto> getAllCompanies() {

        List<Company> companies= companyRepository.findAllByJobStatus(ApplicationConstant.STATUS_ACTIVE);
        List<CompanyDto> dto=companies.stream().map(this::transformCompanyToDto).toList();
        return dto;
    }

    @Override
    @Cacheable("companiesAdmin")
    public List<CompanyDto> getAllCompaniesForAdmin() {

        List<Company> companies= companyRepository.findAll();
        List<CompanyDto> dto=companies.stream().map(this::transformCompanyToDtoAdmin).toList();
        return dto;
    }

    // Builds a new Company entity from the incoming DTO, saves it, and returns the persisted row as a DTO.
    @Override
    @Transactional
    @CacheEvict(value = {"companiesPublic", "companiesAdmin"}, allEntries = true)
    public boolean createCompany(CompanyDto companyDto) {
        Company company = transformDtoToCompany(companyDto);
        Company savedCompany = companyRepository.save(company);
        return savedCompany.getId() != null && savedCompany.getId() > 1;
    }

    // Updates the company identified by id with the fields present in companyDto via a
    // bulk JPQL update (CompanyRepository.updateCompany), leaving id/createdAt/jobs untouched.
    @Override
    @Transactional
    @CacheEvict(value = {"companiesPublic", "companiesAdmin"}, allEntries = true)
    public boolean updateCompany(Long id, CompanyDto companyDto) {
        int rowsAffected = companyRepository.updateCompany(
                id,
                companyDto.name(),
                companyDto.logo(),
                companyDto.industry(),
                companyDto.size(),
                companyDto.rating(),
                companyDto.locations(),
                companyDto.founded(),
                companyDto.description(),
                companyDto.employees(),
                companyDto.website()
        );
        return rowsAffected > 0;
    }

    // Deletes the company identified by id, if one exists. Since Company.jobs cascades
    // ALL/orphanRemoval, deleting a company also deletes its associated jobs.
    @Override
    @Transactional
    @CacheEvict(value = {"companiesPublic", "companiesAdmin"}, allEntries = true)
    public boolean deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            return false;
        }
        companyRepository.deleteById(id);
        return true;
    }

    // Copies the writable fields of a CompanyDto into a brand-new Company entity.
    // id/createdAt/updatedAt/createdBy/updatedBy are left untouched: id is DB-generated,
    // and the audit columns are populated by Spring Data JPA auditing on insert.
    // jobs is intentionally not copied here - a new company starts with no jobs, and
    // jobs are created/attached separately through the Job side of the relationship.
    private Company transformDtoToCompany(CompanyDto companyDto) {
        Company company = new Company();
        company.setName(companyDto.name());
        company.setLogo(companyDto.logo());
        company.setIndustry(companyDto.industry());
        company.setSize(companyDto.size());
        company.setRating(companyDto.rating());
        company.setLocations(companyDto.locations());
        company.setFounded(companyDto.founded());
        company.setDescription(companyDto.description());
        company.setEmployees(companyDto.employees());
        company.setWebsite(companyDto.website());
        return company;
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
    private CompanyDto transformCompanyToDtoAdmin(Company company) {
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
               null);
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

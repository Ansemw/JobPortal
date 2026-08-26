package com.backend.jobportal.company.service.impl;

import com.backend.jobportal.company.dto.CompanyDto;
import com.backend.jobportal.company.repository.CompanyRepository;
import com.backend.jobportal.company.service.ICompanyService;
import com.backend.jobportal.entity.Company;
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

        List<Company> companies= companyRepository.findAll();
        List<CompanyDto> dto=companies.stream().map(this::transaformToDto).toList();
        return dto;
    }

    // Copies the fields of a Company entity into a new CompanyDto.
    private CompanyDto transaformToDto(Company company) {
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
                company.getCreatedAt()
        );
    }

}

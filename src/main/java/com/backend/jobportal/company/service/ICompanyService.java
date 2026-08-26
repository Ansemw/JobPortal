package com.backend.jobportal.company.service;

import com.backend.jobportal.company.dto.CompanyDto;
import com.backend.jobportal.entity.Company;

import java.util.List;

public interface ICompanyService {

    // Returns every company in the system as a list of DTOs.
    public List<CompanyDto> getAllCompanies();
}

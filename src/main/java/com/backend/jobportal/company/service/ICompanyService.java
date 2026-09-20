package com.backend.jobportal.company.service;

import com.backend.jobportal.company.dto.CompanyDto;
import com.backend.jobportal.entity.Company;

import java.util.List;

public interface ICompanyService {

    // Returns every company in the system as a list of DTOs.
    public List<CompanyDto> getAllCompanies();

    public List<CompanyDto> getAllCompaniesForAdmin();

    // Persists a new company from the given DTO. Returns true if the saved company's
    // generated id is greater than one (indicating a successful insert), false otherwise.
    public boolean createCompany(CompanyDto companyDto);

    // Updates the company identified by id with the fields present in the given DTO.
    // Returns true if a matching row was updated, false if no company exists with that id.
    public boolean updateCompany(Long id, CompanyDto companyDto);

    // Deletes the company identified by id. Returns true if a matching company existed
    // and was deleted, false if no company exists with that id.
    public boolean deleteCompany(Long id);
}

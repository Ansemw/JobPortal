package com.backend.jobportal.company.controller;

import com.backend.jobportal.company.dto.CompanyDto;
import com.backend.jobportal.company.service.ICompanyService;
import com.backend.jobportal.entity.Company;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final ICompanyService companyService;

    /*@Autowired
    public CompanyController(ICompanyService companyService) {
        this.companyService = companyService;
    }*/
    // Handles GET requests for the list of all companies and returns them as DTOs.
    @GetMapping(path = "/public", version = "1.0")
    public ResponseEntity<List<CompanyDto>> getAllCompanies() {
        List<CompanyDto> companies = companyService.getAllCompanies();
        return  ResponseEntity.ok().body(companies);
    }
    @GetMapping(path = "/admin", version = "1.0")
    public ResponseEntity<List<CompanyDto>> getAllCompaniesAdmin() {
        List<CompanyDto> companies = companyService.getAllCompaniesForAdmin();
        return  ResponseEntity.ok().body(companies);
    }

    // Handles POST requests to create a new company from the validated request body.
    // Returns true (201) if the save produced a valid generated id, false (unprocessable) otherwise.
    @PostMapping(path = "/admin", version = "1.0")
    public ResponseEntity<String> createNewCompany(@RequestBody @Valid CompanyDto companyDto) {
        boolean isSaved = companyService.createCompany(companyDto);
        if (isSaved) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Request processed successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Request failure");
    }

    // Handles PUT requests to update an existing company's details.
    // Returns 200 if a company with the given id was found and updated, 404 otherwise.
    @PutMapping(path = "/{id}/admin", version = "1.0")
    public ResponseEntity<String> updateCompany(@PathVariable Long id, @RequestBody @Valid CompanyDto companyDto) {
        boolean isUpdated = companyService.updateCompany(id, companyDto);
        if (isUpdated) {
            return ResponseEntity.ok("Request processed successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found");
    }

    // Handles DELETE requests to remove a company by id.
    // Returns 200 if a company with the given id was found and deleted, 404 otherwise.
    @DeleteMapping(path = "/{id}/admin", version = "1.0")
    public ResponseEntity<String> deleteCompany(@PathVariable Long id) {
        boolean isDeleted = companyService.deleteCompany(id);
        if (isDeleted) {
            return ResponseEntity.ok("Request processed successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found");
    }
}

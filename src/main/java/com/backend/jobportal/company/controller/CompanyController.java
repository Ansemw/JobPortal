package com.backend.jobportal.company.controller;

import com.backend.jobportal.company.dto.CompanyDto;
import com.backend.jobportal.company.service.ICompanyService;
import com.backend.jobportal.entity.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}

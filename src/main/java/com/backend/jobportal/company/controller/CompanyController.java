package com.backend.jobportal.company.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
public class CompanyController {

    @GetMapping(version = "1.0")
    public ResponseEntity<String> getAllCompanies() {
        return  ResponseEntity.ok().body("All Companies");
    }

}

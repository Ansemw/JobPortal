package com.backend.jobportal.job.controller;

import com.backend.jobportal.job.dto.JobDto;
import com.backend.jobportal.job.service.IJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final IJobService jobService;

    // Handles GET requests for the list of all jobs and returns them as DTOs.
    @GetMapping(version = "1.0")
    public ResponseEntity<List<JobDto>> getAllJobs() {
        List<JobDto> jobs = jobService.getAllJobs();
        return ResponseEntity.ok().body(jobs);
    }

    @GetMapping(path = "/employer", version = "1.0")
    public ResponseEntity<List<JobDto>> getEmployersJob(Authentication authentication) {
        String email = authentication.getName();
        List<JobDto> dto = jobService.getEmployerJob(email);
        return ResponseEntity.ok().body(dto);
    }
}

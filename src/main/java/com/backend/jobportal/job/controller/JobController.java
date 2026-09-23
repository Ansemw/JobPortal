package com.backend.jobportal.job.controller;

import com.backend.jobportal.job.dto.JobDto;
import com.backend.jobportal.job.service.IJobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    // Handles PATCH requests to update a job's status, scoped to the logged-in employer's own company.
    @PatchMapping(path = "/{jobId}/status/employer", version = "1.0")
public ResponseEntity<?> updateJobStatus(@PathVariable Long jobId, @RequestBody Map<String, String> requestBody, Authentication authentication) {
        String status = requestBody.get("status");

        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Status is required"));
        }
        JobDto updatedJob = jobService.updateJobStatus(jobId, status, authentication.getName());
        return ResponseEntity.ok(updatedJob);
    }

    // Handles POST requests to create a new job posting for the logged-in employer's own company.
    @PostMapping(path = "/employer", version = "1.0")
    public ResponseEntity<JobDto> createJob(@RequestBody @Valid JobDto jobDto, Authentication authentication) {
        JobDto savedJob = jobService.createJob(jobDto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedJob);
    }
}

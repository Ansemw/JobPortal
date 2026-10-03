package com.backend.jobportal.jobactions.controller;

import com.backend.jobportal.job.dto.JobDto;
import com.backend.jobportal.jobactions.dto.ApplyJobRequestDto;
import com.backend.jobportal.jobactions.dto.JobApplicationDto;
import com.backend.jobportal.jobactions.service.IJobActionsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job-actions")
@RequiredArgsConstructor
public class JobActionsController {

    private final IJobActionsService jobActionsService;

    @PutMapping(path = "/saved-jobs/{jobId}/jobseeker", version = "1.0")
    public ResponseEntity<JobDto> saveJob(@PathVariable Long jobId, Authentication authentication) {

        String email = authentication.getName();
        JobDto savedJob = jobActionsService.saveJob(email, jobId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedJob);
    }

    @DeleteMapping(path = "/saved-jobs/{jobId}/jobseeker", version = "1.0")
    public ResponseEntity<String> unSaveJob(@PathVariable Long jobId, Authentication authentication) {

        String email = authentication.getName();
        jobActionsService.unSaveJob(email, jobId);
        return ResponseEntity.status(HttpStatus.OK).body("Job Unsaved Successfully");
    }

    @GetMapping(path = "/saved-jobs/jobseeker", version = "1.0")
    public ResponseEntity<List<JobDto>> getSavedJobs(Authentication authentication) {

        String email = authentication.getName();
        List<JobDto> list=jobActionsService.getSavedJobs(email);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PostMapping(path = "/job-application/jobseeker", version = "1.0")
    public ResponseEntity<JobApplicationDto> applyForJob(@RequestBody @Valid ApplyJobRequestDto applyJobRequestDto
                                                                                    , Authentication authentication) {

        String email = authentication.getName();
        JobApplicationDto applicationDto=jobActionsService.applyForJob(email, applyJobRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(applicationDto);
    }

    @DeleteMapping(path = "/job-application/{jobId}/jobseeker", version = "1.0")
    public ResponseEntity<String> withdrawApplication(@PathVariable Long jobId, Authentication authentication) {
        String email = authentication.getName();
        jobActionsService.withdrawApplication(email, jobId);
        return ResponseEntity.status(HttpStatus.OK).body("Job Withdrawn Successfully");
    }

    @GetMapping(path = "/job-application/jobseeker", version = "1.0")
    public ResponseEntity<List<JobApplicationDto>> getJobApplications(Authentication authentication) {

        String email = authentication.getName();
        List<JobApplicationDto> applications= jobActionsService.getJobApplications(email);
        return ResponseEntity.ok(applications);
    }
}



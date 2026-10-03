package com.backend.jobportal.jobactions.service;

import com.backend.jobportal.job.dto.JobDto;
import com.backend.jobportal.jobactions.dto.ApplyJobRequestDto;
import com.backend.jobportal.jobactions.dto.JobApplicationDto;

import java.util.List;

public interface IJobActionsService {

    // Adds the job to the logged-in user's saved jobs (idempotent) and returns the saved job.
    JobDto saveJob(String email, Long jobId);

    // Removes the job from the logged-in user's saved jobs (idempotent: a no-op if it wasn't saved).
    void unSaveJob(String email, Long jobId);

    // Returns every job the logged-in user has saved, as DTOs.
    List<JobDto> getSavedJobs(String email);

    // Creates a job application for the logged-in user and returns it as a DTO.
    JobApplicationDto applyForJob(String email, ApplyJobRequestDto applyJobRequestDto);

    // Deletes the logged-in user's application for the given job and decrements the job's applications count.
    void withdrawApplication(String email, Long jobId);

    // Returns every application the logged-in user has made, most recent first.
    List<JobApplicationDto> getJobApplications(String email);

}

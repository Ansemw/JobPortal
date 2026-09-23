package com.backend.jobportal.job.service;

import com.backend.jobportal.job.dto.JobDto;

import java.util.List;

public interface IJobService {

    // Returns every job in the system as a list of DTOs.
    public List<JobDto> getAllJobs();

    // Returns the jobs posted by the employer's own company, looked up by the employer's email.
    public List<JobDto> getEmployerJob(String email);

    // Updates a job's status, scoped to the logged-in employer's own company, returning the updated job.
    public JobDto updateJobStatus(Long jobId, String status, String email);

    // Creates a new job posting for the logged-in employer's own company, returning the saved job.
    public JobDto createJob(JobDto jobDto, String email);
}

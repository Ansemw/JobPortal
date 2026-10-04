package com.backend.jobportal.jobactions.service.impl;

import com.backend.jobportal.constants.ApplicationConstant;
import com.backend.jobportal.entity.Job;
import com.backend.jobportal.entity.JobApplication;
import com.backend.jobportal.entity.JobPortalUser;
import com.backend.jobportal.job.dto.JobDto;
import com.backend.jobportal.job.repository.JobRepository;
import com.backend.jobportal.job.util.JobUtil;
import com.backend.jobportal.jobactions.dto.ApplyJobRequestDto;
import com.backend.jobportal.jobactions.dto.JobApplicationDto;
import com.backend.jobportal.jobactions.dto.UpdateJobApplicationDto;
import com.backend.jobportal.jobactions.repository.JobApplicationRepository;
import com.backend.jobportal.user.profile.util.ProfileUtil;
import com.backend.jobportal.jobactions.service.IJobActionsService;
import com.backend.jobportal.user.repository.JobPortalUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JobActionsServiceImpl implements IJobActionsService {

    private final JobPortalUserRepository jobPortalUserRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    @Override
    @Transactional
    public JobDto saveJob(String email, Long jobId) {
        JobPortalUser user = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!"ACTIVE".equals(job.getStatus())) {
            throw new RuntimeException("Only active jobs can be saved");
        }

        // savedJobs is a Set, so saving an already-saved job is a no-op.
        user.getSavedJobs().add(job);
        // Not strictly necessary: the user is already managed within this transaction, so
        // Hibernate's dirty checking would write the saved_jobs row on commit anyway. The
        // explicit save() is only here to make the persistence intent obvious to readers.
        jobPortalUserRepository.save(user);
        return JobUtil.transformJobToDto(job);
    }

    @Override
    @Transactional
    public void unSaveJob(String email, Long jobId) {
        JobPortalUser user = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Match by id rather than looking the Job up, so a job that has since been closed or
        // deleted can still be removed from the user's saved list. Removing a job that isn't
        // saved is a no-op.
        //user.getSavedJobs().removeIf(job -> job.getId().equals(jobId));

        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
        user.getSavedJobs().remove(job);

        // Redundant for the same reason as in saveJob: the managed user's collection change is
        // flushed on commit anyway; kept for readability.
        jobPortalUserRepository.save(user);
    }

    // Read-only (class default): savedJobs is lazy, so it's loaded here inside the transaction.
    // Every saved job is returned regardless of its current status, so a job that was closed
    // after being saved still shows up in the user's list.
    @Override
    public List<JobDto> getSavedJobs(String email) {
        JobPortalUser user = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getSavedJobs().stream().map(JobUtil::transformJobToDto).toList();
    }

    @Override
    @Transactional
    public JobApplicationDto applyForJob(String email, ApplyJobRequestDto applyJobRequestDto) {

        JobPortalUser user =  jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(applyJobRequestDto.jobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setUser(user);
        application.setAppliedAt(Instant.now());
        application.setStatus(ApplicationConstant.STATUS_PENDING);
        application.setCoverLetter(applyJobRequestDto.coverLetter());
        JobApplication savedApplication = jobApplicationRepository.save(application);
        Integer applicationsCount = job.getApplicationsCount();
        job.setApplicationsCount(applicationsCount ==null? 1: applicationsCount+ 1);

        return transformApplicationToDto(savedApplication);
    }

    @Override
    @Transactional
    public void withdrawApplication(String email, Long jobId) {
        JobPortalUser user = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jobApplicationRepository.existsByUserIdAndJobId(user.getId(), jobId)) {
            throw new RuntimeException("You haven't applied for this job");
        }

        jobApplicationRepository.deleteByUserIdAndJobId(user.getId(), jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        Integer applicationsCount = job.getApplicationsCount();
        job.setApplicationsCount(applicationsCount == null || applicationsCount <= 0 ? 0 : applicationsCount - 1);
    }

    // Read-only (class default): user.profile and job.company are lazy, so they're resolved inside the transaction.
    @Override
    public List<JobApplicationDto> getJobApplications(String email) {
        JobPortalUser user = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jobApplicationRepository.findByUserIdOrderByAppliedAtDesc(user.getId()).stream()
                .map(this::transformApplicationToDto)
                .toList();
    }

    // Read-only (class default). The job must belong to the calling employer's own company.
    @Override
    public List<JobApplicationDto> getJobApplicationsByJob(String email, Long jobId) {
        JobPortalUser employer = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (employer.getCompany() == null) {
            throw new RuntimeException("Employer is not associated with a company");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getCompany().getId().equals(employer.getCompany().getId())) {
            throw new RuntimeException("This job doesn't belong to your company");
        }

        return jobApplicationRepository.findByJobIdOrderByAppliedAtDesc(jobId).stream()
                .map(this::transformApplicationToDto)
                .toList();
    }

    @Override
    @Transactional
    public boolean updateJobApplicationStatus(String email, UpdateJobApplicationDto updateJobApplicationDto) {
        JobPortalUser employer = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (employer.getCompany() == null) {
            throw new RuntimeException("Employer is not associated with a company");
        }

        // The query only matches applications of the employer's own company and keeps the stored
        // notes when notes is null, so 0 rows means the application doesn't exist or isn't theirs.
        int updatedRows = jobApplicationRepository.updateStatusById(
                updateJobApplicationDto.applicationId(),
                updateJobApplicationDto.status().name(),
                updateJobApplicationDto.notes(),
                email,
                employer.getCompany().getId());
        return updatedRows > 0;
    }

    private JobApplicationDto transformApplicationToDto(JobApplication application) {
        JobPortalUser user = application.getUser();

        return new JobApplicationDto(
                application.getId(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getMobileNumber(),
                ProfileUtil.transformProfileToDto(user.getProfile(), true),
                JobUtil.transformJobToDto(application.getJob()),
                application.getAppliedAt(),
                application.getStatus(),
                application.getCoverLetter(),
                application.getNotes());
    }
}

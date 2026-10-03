package com.backend.jobportal.jobactions.repository;

import com.backend.jobportal.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Optional<JobApplication> findByUserIdAndJobId(Long userId, Long jobId);

    List<JobApplication> findByUserIdOrderByAppliedAtDesc(Long userId);

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    // Bulk delete; returns the number of rows removed (0 if the user hadn't applied to the job).
    @Modifying
    @Query("delete from JobApplication ja where ja.user.id = :userId and ja.job.id = :jobId")
    void deleteByUserIdAndJobId(@Param("userId") Long userId, @Param("jobId") Long jobId);
}

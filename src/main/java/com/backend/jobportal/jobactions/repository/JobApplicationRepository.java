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

    List<JobApplication> findByJobIdOrderByAppliedAtDesc(Long jobId);

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    // Bulk update scoped to the given company's jobs; returns the number of rows updated (0 if the
    // application doesn't exist or its job belongs to another company). A null notes keeps the existing
    // notes. A bulk update bypasses JPA auditing, so updated_at/updated_by are set explicitly.
    @Modifying
    @Query("update JobApplication ja set ja.status = :status, ja.notes = coalesce(:notes, ja.notes), " +
            "ja.updatedAt = CURRENT_TIMESTAMP, ja.updatedBy = :updatedBy " +
            "where ja.id = :applicationId " +
            "and ja.job.id in (select j.id from Job j where j.company.id = :companyId)")
    int updateStatusById(@Param("applicationId") Long applicationId,
                         @Param("status") String status,
                         @Param("notes") String notes,
                         @Param("updatedBy") String updatedBy,
                         @Param("companyId") Long companyId);

    // Bulk delete; returns the number of rows removed (0 if the user hadn't applied to the job).
    @Modifying
    @Query("delete from JobApplication ja where ja.user.id = :userId and ja.job.id = :jobId")
    void deleteByUserIdAndJobId(@Param("userId") Long userId, @Param("jobId") Long jobId);
}

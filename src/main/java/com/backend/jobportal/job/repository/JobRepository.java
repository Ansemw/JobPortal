package com.backend.jobportal.job.repository;

import com.backend.jobportal.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByCompanyId(Long companyId);

    @Modifying(clearAutomatically = true)
    @Query("Update Job j set j.status = :status, j.updatedAt = CURRENT_TIMESTAMP, j.updatedBy = :updatedBy where j.id = :id")
    int updateStatusById(@Param("id") Long id, @Param("status") String status, @Param("updatedBy") String updatedBy);
}

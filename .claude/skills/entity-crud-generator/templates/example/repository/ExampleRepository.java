package com.backend.jobportal.example.repository;

import com.backend.jobportal.entity.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExampleRepository extends JpaRepository<Example, Long> {

    // Derived query — Spring Data builds it from the method name (company.id).
    List<Example> findByCompanyId(Long companyId);

    // Bulk update. clearAutomatically = true is required whenever the same @Transactional method
    // re-reads the entity afterwards to return a DTO; otherwise findById returns the stale cached copy.
    @Modifying(clearAutomatically = true)
    @Query("Update Example e set e.status = :status, e.updatedAt = CURRENT_TIMESTAMP, e.updatedBy = :updatedBy where e.id = :id")
    int updateStatusById(@Param("id") Long id, @Param("status") String status, @Param("updatedBy") String updatedBy);
}

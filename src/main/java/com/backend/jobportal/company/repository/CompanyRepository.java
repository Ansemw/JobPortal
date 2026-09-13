package com.backend.jobportal.company.repository;

import com.backend.jobportal.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

   // @Query("Select distinct c from Company c join fetch c.jobs j where j.status = :status")
    List<Company> findAllByJobStatus(@Param("status") String status);

    // Implementation of native sql query. Just for knowledge, rarely used due to performance issues.
    @Query(value = "Select distinct c.* from companies c join  jobs j on c.id = j.company_id where j.status = ?",
    nativeQuery = true)
    List<Company> findAllByJobStatusNative(String status);
}

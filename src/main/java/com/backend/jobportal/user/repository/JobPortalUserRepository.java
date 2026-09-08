package com.backend.jobportal.user.repository;

import com.backend.jobportal.entity.JobPortalUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobPortalUserRepository extends JpaRepository<JobPortalUser, Long> {

    Optional<JobPortalUser> findByEmailOrMobileNumber(String name, String mobileNumber);

    Optional<JobPortalUser> findByEmail(String email);
}

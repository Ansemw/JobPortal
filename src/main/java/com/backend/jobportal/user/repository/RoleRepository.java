package com.backend.jobportal.user.repository;

import com.backend.jobportal.entity.JobPortalUser;
import com.backend.jobportal.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findRoleByName(String name);
}

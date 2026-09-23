package com.backend.jobportal.user.repository;

import com.backend.jobportal.entity.JobPortalUser;
import com.backend.jobportal.entity.Role;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Cacheable("roles")
    Optional<Role> findRoleByName(String name);
}

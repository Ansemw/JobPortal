package com.backend.jobportal.user.service.impl;

import com.backend.jobportal.company.repository.CompanyRepository;
import com.backend.jobportal.constants.ApplicationConstant;
import com.backend.jobportal.entity.Company;
import com.backend.jobportal.entity.JobPortalUser;
import com.backend.jobportal.entity.Role;
import com.backend.jobportal.user.dto.UserDto;
import com.backend.jobportal.user.repository.JobPortalUserRepository;
import com.backend.jobportal.user.repository.RoleRepository;
import com.backend.jobportal.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements IUserService {

    private final JobPortalUserRepository jobPortalUserRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public UserServiceImpl(JobPortalUserRepository jobPortalUserRepository, RoleRepository roleRepository,
                            CompanyRepository companyRepository) {
        this.jobPortalUserRepository = jobPortalUserRepository;
        this.roleRepository = roleRepository;
        this.companyRepository = companyRepository;
    }

    // Looks up a user by email and converts it into a UserDto, if found.
    @Override
    public Optional<UserDto> findUserByEmail(String email) {
        return jobPortalUserRepository.findByEmail(email).map(this::transformUserToDto);
    }

    // Assigns ROLE_EMPLOYER to the user identified by userId, if one exists.
    // Already-ROLE_EMPLOYER users are returned unchanged, ROLE_ADMIN users are rejected,
    // and only ROLE_JOB_SEEKER users are actually elevated.
    @Override
    @Transactional
    public Optional<UserDto> elevateUserToEmployer(Long userId) {
        return jobPortalUserRepository.findById(userId).map(user -> {
            String currentRole = user.getRole().getName();

            if (ApplicationConstant.ROLE_EMPLOYER.equals(currentRole)) {
                return transformUserToDto(user);
            }

            if (ApplicationConstant.ROLE_ADMIN.equals(currentRole)) {
                throw new RuntimeException("The ADMIN role can't be changed");
            }

            Role employerRole = roleRepository.findRoleByName(ApplicationConstant.ROLE_EMPLOYER).get();
            user.setRole(employerRole);
            JobPortalUser savedUser = jobPortalUserRepository.save(user);
            return transformUserToDto(savedUser);
        });
    }

    // Associates the user identified by userId (who must already hold ROLE_EMPLOYER) with
    // the company identified by companyId. The FK update bypasses the entity (see
    // JobPortalUserRepository.updateUserCompany), so the returned DTO's company fields are
    // populated directly from the freshly-fetched Company rather than the stale user.company.
    @Override
    @Transactional
    public Optional<UserDto> assignCompanyToUser(Long userId, Long companyId) {
        return jobPortalUserRepository.findById(userId).map(user -> {
            if (!ApplicationConstant.ROLE_EMPLOYER.equals(user.getRole().getName())) {
                throw new RuntimeException("Only users with ROLE_EMPLOYER can be associated with a company");
            }

            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

            user.setCompany(company);
            JobPortalUser savedUser = jobPortalUserRepository.save(user);
            return transformUserToDto(savedUser);
        });
    }

    // Copies the fields of a JobPortalUser entity into a new UserDto.
    private UserDto transformUserToDto(JobPortalUser user) {
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setMobileNumber(user.getMobileNumber());
        userDto.setRole(user.getRole().getName());
        if (user.getCompany() != null) {
            userDto.setCompanyId(user.getCompany().getId());
            userDto.setCompanyName(user.getCompany().getName());
        }
        userDto.setCreatedAt(user.getCreatedAt());
        return userDto;
    }
}

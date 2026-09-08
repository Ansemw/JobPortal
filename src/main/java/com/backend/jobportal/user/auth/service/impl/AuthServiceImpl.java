package com.backend.jobportal.user.auth.service.impl;

import com.backend.jobportal.constants.ApplicationConstant;
import com.backend.jobportal.entity.Role;
import com.backend.jobportal.user.auth.dto.RegisterRequestDto;
import com.backend.jobportal.user.auth.service.IAuthService;
import com.backend.jobportal.entity.JobPortalUser;
import com.backend.jobportal.user.repository.JobPortalUserRepository;
import com.backend.jobportal.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final PasswordEncoder passwordEncoder;

    private final JobPortalUserRepository jobPortalUserRepository;

    private final RoleRepository roleRepository;

    private final CompromisedPasswordChecker compromisedPasswordChecker;

    @Override
    public Map<String, String> apiRegister(RegisterRequestDto registerRequestDto) {

        Map<String, String> response = new HashMap<>();

        CompromisedPasswordDecision decision = compromisedPasswordChecker.check(registerRequestDto.password());
        if(decision.isCompromised()) {
            response.put("password", "stronger password needed");
            return response;
        }
       Optional<JobPortalUser> existingUser = jobPortalUserRepository.findByEmailOrMobileNumber(registerRequestDto.email()
                                                , registerRequestDto.mobileNumber());


       if (existingUser.isPresent()) {
           if(existingUser.get().getEmail().equals(registerRequestDto.email())) {
               response.put("Email", "Email Already Exists");
           }
           if(existingUser.get().getMobileNumber().equals(registerRequestDto.mobileNumber())) {
               response.put("Mobile Number", "Mobile Number Already Exists");
           }
           return response;
       }
        JobPortalUser jobPortalUser =new JobPortalUser();
        BeanUtils.copyProperties(registerRequestDto,jobPortalUser);
        jobPortalUser.setPasswordHash(passwordEncoder.encode(registerRequestDto.password()));

        Role role = roleRepository.findRoleByName(ApplicationConstant.ROLE_JOB_SEEKER)
                .orElseThrow(() ->new IllegalArgumentException(ApplicationConstant.ROLE_JOB_SEEKER
                        + " is Invalid Role"));
        jobPortalUser.setRole(role);
        jobPortalUserRepository.save(jobPortalUser);

        response.put(ApplicationConstant.STATUS_SUCCESS, "User Registered");
        return response;
    }
}

package com.backend.jobportal.aspect;

import com.backend.jobportal.entity.JobPortalUser;
import com.backend.jobportal.exception.RegistrationValidationException;
import com.backend.jobportal.user.auth.dto.RegisterRequestDto;
import com.backend.jobportal.user.repository.JobPortalUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RegistrationValidationAspect {

    private final CompromisedPasswordChecker compromisedPasswordChecker;

    private final JobPortalUserRepository jobPortalUserRepository;

    @Before("execution(* com.backend.jobportal.user.auth.service.impl.AuthServiceImpl.apiRegister(..))")
    public void validateBeforeRegistration(JoinPoint joinPoint) {

        Object[] methodArgs = joinPoint.getArgs();
        var registerRequestDto = (RegisterRequestDto)methodArgs[0];
        log.info("Validating user registration request");
        Map<String, String> response = new HashMap<>();

        CompromisedPasswordDecision decision = compromisedPasswordChecker.check(registerRequestDto.password());
        if(decision.isCompromised()) {
            response.put("password", "stronger password needed");
        }
        Optional<JobPortalUser> existingUser = jobPortalUserRepository.findByEmailOrMobileNumber(registerRequestDto.email()
                , registerRequestDto.mobileNumber());


        if (existingUser.isPresent()) {
            if(existingUser.get().getEmail().equalsIgnoreCase(registerRequestDto.email())) {
                response.put("Email", "Email Already Exists");
            }
            if(existingUser.get().getMobileNumber().equals(registerRequestDto.mobileNumber())) {
                response.put("Mobile Number", "Mobile Number Already Exists");
            }
        }
        if(!response.isEmpty()){
           log.error("registration failed with error {}",response.toString());
           throw new RegistrationValidationException(response);
        }

        log.info("user registration validated");
    }
}

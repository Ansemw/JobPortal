package com.backend.jobportal.aspect;

import com.backend.jobportal.user.auth.dto.LoginResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoginSuccessAspect {

    @AfterReturning(
            pointcut = "execution(* com.backend.jobportal.user.auth.controller.AuthController.apiLogin(..))",
            returning = "response"
    )
    public void successfulLogin(JoinPoint joinPoint, Object response){

        if(!(response instanceof ResponseEntity<?> responseEntity)){
            return;
        }
        log.info("responseEntity found");
        Object body = responseEntity.getBody();

        if(! (body instanceof LoginResponseDto loginResponseDto)){
            return;
        }
        log.info("LoginResponseDto found");
        if(loginResponseDto.userDto()!=null){
            log.info("user login successful for username {} | role {}"
                    ,loginResponseDto.userDto().getName(), loginResponseDto.userDto().getRole());
        }
        log.info("userDto found");
    }
}

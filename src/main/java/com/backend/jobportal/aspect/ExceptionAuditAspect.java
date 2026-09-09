package com.backend.jobportal.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionAuditAspect {

    @AfterThrowing(
            pointcut = "execution(* com.backend.jobportal..*.*(..))",
            throwing = "exception"
    )
    public void logAfterException(JoinPoint joinPoint, Exception exception){
        String methodName = joinPoint.getSignature().toShortString();
        Object[] methodArgs = joinPoint.getArgs();

        log.error("Exception occurred in method {}| with arguments {}",methodName,methodArgs);
    }
}

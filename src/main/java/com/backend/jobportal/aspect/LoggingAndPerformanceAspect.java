package com.backend.jobportal.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Aspect
@Component
@Slf4j
public class LoggingAndPerformanceAspect {

    @Around("execution(* com.backend.jobportal..*.*(..))")
    public Object logAndMeasureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        Object[] methodArgs = joinPoint.getArgs();
        log.info("Entering method : {}", methodName);
        log.info("Method args : {}", Arrays.toString(methodArgs));
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;
        log.info("{} executed successfully in {} ms", methodName, executionTime);
        return result;
    }
}

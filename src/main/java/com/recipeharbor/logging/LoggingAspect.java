package com.recipeharbor.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Aspect
public class LoggingAspect {
    @Around("execution(* com.recipeharbor..*(..)) && !execution(* com.recipeharbor.dto..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Entering : {}", joinPoint.getSignature().toShortString());
        Object result = joinPoint.proceed();
        log.debug("Exiting : {}", joinPoint.getSignature().toShortString());
        return result;
    }
}

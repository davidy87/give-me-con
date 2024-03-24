package com.givemecon.util.aspect.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* com.givemecon.config.auth.*Authentication*Handler.*(..))")
    private void allAuthenticationHandlers() {}

    @Pointcut("within(com.givemecon..*Controller)")
    private void allControllers() {}

    @Pointcut("within(com.givemecon..*Service)")
    private void allServices() {}

    @Pointcut("within(com.givemecon..*Repository)")
    private void allRepositories() {}

    @Around("allAuthenticationHandlers() || allControllers() || allServices() || allRepositories()")
    public Object doTraceLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[Log] --- Begin {} ---", joinPoint.getSignature().getDeclaringTypeName());
        Object result = joinPoint.proceed();
        log.info("[Log] --- End {} ---", joinPoint.getSignature().getDeclaringTypeName());
        return result;
    }
}

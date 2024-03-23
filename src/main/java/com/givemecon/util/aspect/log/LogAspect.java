package com.givemecon.util.aspect.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class LogAspect {

    @Pointcut("execution(* com.givemecon.config.auth.*Authentication*Handler.*(..))")
    private void allAuthenticationHandler() {}

    @Pointcut("execution(* com.givemecon..*Controller.*(..))")
    private void allController() {}

    @Pointcut("execution(* com.givemecon..*Service.*(..))")
    private void allService() {}

    @Pointcut("execution(* com.givemecon..*Repository.*(..))")
    private void allRepository() {}

    @Around("allAuthenticationHandler() || allController() || allService() || allRepository()")
    public Object doTraceLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[Log] Begin {}", joinPoint.getSignature().getDeclaringTypeName());
        Object result = joinPoint.proceed();
        log.info("[Log] End {}", joinPoint.getSignature().getDeclaringTypeName());
        return result;
    }
}

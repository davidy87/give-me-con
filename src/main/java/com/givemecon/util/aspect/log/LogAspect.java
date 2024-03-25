package com.givemecon.util.aspect.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Before("com.givemecon.util.aspect.log.LogPointcut.allHandlersAndLayers()")
    public void doBeforeLog(JoinPoint joinPoint) {
        log.info("[Log] --- Begin {} ---", joinPoint.getSignature().getDeclaringTypeName());
        Arrays.stream(joinPoint.getArgs()).forEach(arg -> {
            if (arg instanceof AuthenticationException e) {
                log.info("[Log] Authentication failed due to {}", e.getClass().getSimpleName(), e);
            }
            if (arg instanceof Authentication auth) {
                log.info("[Log] Login Username = {}", auth.getName());
            }
        });
    }

    @AfterThrowing(value = "com.givemecon.util.aspect.log.LogPointcut.allAuthenticationHandlers()", throwing = "e")
    public void doExceptionLog(Exception e) {
        log.info("[Log] Caught {}", e.getClass().getSimpleName(), e);
    }

    @After("com.givemecon.util.aspect.log.LogPointcut.allHandlersAndLayers()")
    public void doAfterLog(JoinPoint joinPoint) {
        log.info("[Log] --- End {} ---", joinPoint.getSignature().getDeclaringTypeName());
    }
}

package com.givemecon.common.aspect.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Before("com.givemecon.common.aspect.log.LogPointcut.allHandlersAndLayers()")
    public void doBeforeLog(JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs()).forEach(arg -> {
            if (arg instanceof AuthenticationException e) {
                log.info("[Log] Authentication failed due to {}", e.getClass().getSimpleName(), e);
            }
            if (arg instanceof Authentication auth) {
                log.info("[Log] Login Username = {}", auth.getName());
            }
        });
    }

    @AfterThrowing(value = "com.givemecon.common.aspect.log.LogPointcut.allAuthenticationHandlers()", throwing = "e")
    public void doExceptionLog(Exception e) {
        log.info("[Log] Caught {}", e.getClass().getSimpleName(), e);
    }
}

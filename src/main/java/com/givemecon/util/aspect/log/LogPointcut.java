package com.givemecon.util.aspect.log;

import org.aspectj.lang.annotation.Pointcut;

public class LogPointcut {

    @Pointcut("execution(* com.givemecon.config.auth.*Authentication*Handler.*(..))")
    public void allAuthenticationHandlers() {}

    @Pointcut("within(com.givemecon..*Controller)")
    public void allControllers() {}

    @Pointcut("within(com.givemecon..*Service)")
    public void allServices() {}

    @Pointcut("within(com.givemecon..*Repository)")
    public void allRepositories() {}

    @Pointcut("allAuthenticationHandlers() || allControllers() || allServices() || allRepositories()")
    public void allHandlersAndLayers() {}
}

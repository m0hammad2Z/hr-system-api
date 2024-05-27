package org.hrsys.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.hrsys.model.JwtRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class LoginTrackerAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoginTrackerAspect.class);

    @Pointcut("execution(* org.hrsys.controller.JwtAuthenticationController.createAuthenticationToken(..))")
    public void loadUserByUsername() {
    }

    @Around("loadUserByUsername()")
    public Object logLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed = null;
        JwtRequest jwtRequest = (JwtRequest) joinPoint.getArgs()[0];
        String email = jwtRequest.getEmail();
        try {
            // Before the method is called
            logger.info("An attempt to login was made at " + new Date() + " with email: " + email);

            proceed = joinPoint.proceed();

            // After the method is called
            logger.info("Login was successful at " + new Date() + " with email: " + email);
        } catch (Exception e) {
            // Log the error
            logger.error("Login failed at " + new Date() + " with email: " + email + ". Error: " + e.getMessage());
            throw e;
        }

        return proceed;
    }
}

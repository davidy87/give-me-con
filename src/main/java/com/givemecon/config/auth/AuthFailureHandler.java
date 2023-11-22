package com.givemecon.config.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    private static final String ERROR_PATH = "http://localhost:8081/login?error=true";

    private static final String OAUTH_ERROR_PATH = "http://localhost:8081/login?oauth2Error=true";


    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        if (exception instanceof BadCredentialsException) {
            response.sendRedirect(ERROR_PATH);
        } else if (exception instanceof OAuth2AuthenticationException) {
            log.info("--- In AuthFailureHandler ---");
            response.sendRedirect(OAUTH_ERROR_PATH);
        }
    }
}

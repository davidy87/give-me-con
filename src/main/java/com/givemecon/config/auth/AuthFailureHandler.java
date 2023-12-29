package com.givemecon.config.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    private static final String LOGIN_URI = "http://localhost:8081/login";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(LOGIN_URI)
                .encode(StandardCharsets.UTF_8)
                .queryParam("error", true);

        if (exception instanceof OAuth2AuthenticationException) {
            log.info("--- In AuthFailureHandler ---");
            uriBuilder.queryParam("state", "duplicate-email");
        }

        response.sendRedirect(uriBuilder.toUriString());
    }
}

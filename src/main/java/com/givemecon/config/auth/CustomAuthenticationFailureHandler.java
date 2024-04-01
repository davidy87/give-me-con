package com.givemecon.config.auth;

import com.givemecon.config.auth.util.ClientUrlProperties;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

import static java.nio.charset.StandardCharsets.*;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler {

    private static final String DUPLICATE_EMAIL_ERROR = "duplicate-email";

    private final ClientUrlProperties clientUrlProperties;

    @PostConstruct
    public void init() {
        String exceptionName = OAuth2AuthenticationException.class.getName();
        String url = UriComponentsBuilder.fromHttpUrl(clientUrlProperties.getLoginUrl())
                .queryParam(ERROR, true)
                .queryParam(ERROR_DESCRIPTION, DUPLICATE_EMAIL_ERROR)
                .encode(UTF_8)
                .toUriString();

        setExceptionMappings(Map.of(exceptionName, url));
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        super.onAuthenticationFailure(request, response, exception);
    }
}

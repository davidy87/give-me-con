package com.givemecon.config.auth;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static com.givemecon.config.auth.enums.ClientUrl.LOGIN_URL;
import static java.nio.charset.StandardCharsets.*;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.*;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler {

    @PostConstruct
    public void init() {
        String exceptionName = OAuth2AuthenticationException.class.getName();
        String url = UriComponentsBuilder.fromHttpUrl(LOGIN_URL.getUrl())
                .queryParam(ERROR, true)
                .queryParam(ERROR_DESCRIPTION, "duplicate-email")
                .encode(UTF_8)
                .toUriString();

        super.setExceptionMappings(Map.of(exceptionName, url));
    }

}

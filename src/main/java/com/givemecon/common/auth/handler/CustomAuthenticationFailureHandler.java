package com.givemecon.common.auth.handler;

import com.givemecon.common.auth.util.ClientUrlProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final String ERROR = "error";

    private static final String ERROR_DESCRIPTION = "errorDescription";

    private final ClientUrlProperties clientUrlProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String errorDescription = "";

        if (exception instanceof OAuth2AuthenticationException) {
            errorDescription = ((OAuth2AuthenticationException) exception).getError().getErrorCode();
            errorDescription = StringUtils.replace(errorDescription, "_", "-");
        }

        String redirectUrl = UriComponentsBuilder.fromHttpUrl(clientUrlProperties.getLoginUrl())
                .queryParam(ERROR)
                .queryParam(ERROR_DESCRIPTION, errorDescription)
                .encode(UTF_8)
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}

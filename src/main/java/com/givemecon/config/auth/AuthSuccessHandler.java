package com.givemecon.config.auth;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("--- In AuthSuccessHandler ---");
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grantType", tokenInfo.getGrantType());
        params.add("accessToken", tokenInfo.getAccessToken());
        params.add("refreshToken", tokenInfo.getRefreshToken());
        params.add("username", principal.getName());

        log.info("principal name = {}", principal.getName());
        log.info("principal name = {}", principal.getAttributes());

        String url = UriComponentsBuilder.fromUriString("http://localhost:8081/login")
                .queryParams(params)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, url);
    }
}

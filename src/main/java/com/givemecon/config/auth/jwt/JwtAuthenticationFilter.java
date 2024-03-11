package com.givemecon.config.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import static com.givemecon.config.auth.enums.JwtAuthHeader.*;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String accessTokenHeader = ((HttpServletRequest) request).getHeader(AUTHORIZATION.getKey());
        String accessToken = jwtTokenProvider.retrieveToken(accessTokenHeader);

        log.info("--- In JwtAuthenticationFilter ---");
        log.info("token = {}", accessToken);
        log.info("url = {}", ((HttpServletRequest) request).getRequestURL());

        if (accessToken != null) {
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            log.info("Authenticated User = {}", authentication.getName());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}

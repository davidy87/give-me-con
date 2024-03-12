package com.givemecon.config.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
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
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.*;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String accessTokenHeader = ((HttpServletRequest) request).getHeader(AUTHORIZATION.getKey());
        String accessToken = jwtTokenProvider.retrieveToken(accessTokenHeader);

        log.info("--- In JwtAuthenticationFilter ---");
        log.info("Request URL = {}", ((HttpServletRequest) request).getRequestURL());
        log.info("Access Token = {}", accessToken);

        if (accessToken != null) {
            proceedAuthentication((HttpServletRequest) request, accessToken);
        }

        chain.doFilter(request, response);
    }

    /**
     * 매개변수로 전달받은 access token을 사용해 {@link Authentication}을 생성하고 {@link SecurityContextHolder}에 등록한다. 도중에
     * {@link ExpiredJwtException}이 발생할 경우, 예외를 잡고 refresh token이 담긴 헤더가 요쳥 시에 같이 전달되었는지 확인한다. 해당 헤더가
     * 있다면 access token 재요청 전용 {@link Authentication} 객체를 생성 및 등록하고, 없다면 잡았던 예외를 그대로 던진다.
     * @param request HTTP 요청
     * @param accessToken 요청 시 전달받은 access token
     */
    private void proceedAuthentication(HttpServletRequest request, String accessToken) {
        try {
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Authenticated User = {}", authentication.getName());
        } catch (ExpiredJwtException e) {
            // Refresh Token이 header로 전달되었을 경우, Access Token을 재발급하는 요청이다.
            String refreshTokenHeader = request.getHeader(REFRESH_TOKEN.getKey());
            if (refreshTokenHeader != null) {
                Authentication refreshAuthentication = authenticated(null, null, null);
                SecurityContextHolder.getContext().setAuthentication(refreshAuthentication);
            } else {
                throw e;
            }
        }
    }
}

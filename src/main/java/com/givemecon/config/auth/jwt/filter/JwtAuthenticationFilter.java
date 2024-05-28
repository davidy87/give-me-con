package com.givemecon.config.auth.jwt.filter;

import com.givemecon.config.auth.jwt.token.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static com.givemecon.config.enums.ApiPathPattern.TOKEN_REISSUE_API;
import static com.givemecon.config.enums.JwtAuthHeader.*;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.*;

@Slf4j
@RequiredArgsConstructor
public final class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("[Log] --- Begin JwtAuthenticationFilter ---");
        String accessToken = getAccessTokenFromRequest(request);

        log.info("[Log] Request URL = {}", request.getRequestURL());
        log.info("[Log] Access Token = {}", accessToken);

        if (StringUtils.hasText(accessToken)) {
            proceedAuthentication(accessToken, request);
        }

        filterChain.doFilter(request, response);
        log.info("[Log] --- End JwtAuthenticationFilter ---");
    }

    /**
     * 매개변수로 전달받은 access token을 사용해 {@link Authentication}을 생성하고 {@link SecurityContextHolder}에 등록한다. 도중에
     * {@link ExpiredJwtException}이 발생할 경우, 예외를 잡고 refresh token이 담긴 헤더가 요쳥 시에 같이 전달되었는지 확인한다. 해당 헤더가
     * 있다면 access token 재요청 전용 {@link Authentication} 객체를 생성 및 등록하고, 없다면 잡았던 예외를 그대로 던진다.
     * @param accessToken 요청 시 전달받은 access token
     * @param request HTTP 요청
     */
    private void proceedAuthentication(String accessToken, HttpServletRequest request) {
        try {
            Authentication authentication = jwtTokenService.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[Log] Authenticated User = {}", authentication.getName());
        } catch (ExpiredJwtException e) {
            log.info("[Log] Caught ExpiredJwtException", e);
            log.info("[Log] request URI = {}", request.getRequestURI());

            // 요청 URI가 토큰 재발급 API가 아닐 경우, 예외를 그대로 던짐
            if (!request.getRequestURI().equals(TOKEN_REISSUE_API.getPattern())) {
                throw e;
            }

            // Refresh Token이 header로 전달되었을 경우, 해당 token이 올바른지 확인
            String refreshToken = Optional.ofNullable(request.getHeader(REFRESH_TOKEN.getName()))
                    .map(jwtTokenService::retrieveToken)
                    .filter(StringUtils::hasText)
                    .orElseThrow(() -> e);

            log.info("[Log] Token reissue request");
            log.info("[Log] Refresh Token = {}", refreshToken);

            Authentication refreshAuthentication = authenticated(null, null, null);
            SecurityContextHolder.getContext().setAuthentication(refreshAuthentication);
        }
    }

    private String getAccessTokenFromRequest(HttpServletRequest request) {
        String accessTokenHeader = request.getHeader(AUTHORIZATION.getName());
        return jwtTokenService.retrieveToken(accessTokenHeader);
    }
}

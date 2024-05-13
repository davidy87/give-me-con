package com.givemecon.config.auth.jwt.filter;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.token.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.givemecon.config.enums.ApiPathPattern.AUTH_SUCCESS_API;
import static com.givemecon.config.enums.JwtAuthHeader.*;
import static com.givemecon.config.enums.SessionAttribute.TOKEN_INFO;
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

        if (accessToken != null) {
            proceedAuthentication(accessToken, request);
        }

        filterChain.doFilter(request, response);
        log.info("[Log] --- End JwtAuthenticationFilter ---");
    }

    private String getAccessTokenFromRequest(HttpServletRequest request) {
        String accessTokenHeader = request.getHeader(AUTHORIZATION.getName());
        String accessToken = jwtTokenService.retrieveToken(accessTokenHeader);

        // 로그인 성공 요청일 경우
        if (StringUtils.pathEquals(request.getRequestURI(), AUTH_SUCCESS_API.getPattern())) {
            HttpSession session = request.getSession(false);

            if (session != null) {
                TokenInfo tokenInfo = (TokenInfo) session.getAttribute(TOKEN_INFO.name());

                if (tokenInfo != null) {
                    accessToken = tokenInfo.getAccessToken();
                }
            }
        }

        log.info("[Log] Request URL = {}", request.getRequestURL());
        log.info("[Log] Access Token = {}", accessToken);

        return accessToken;
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
            String refreshTokenHeader = request.getHeader(REFRESH_TOKEN.getName());
            String refreshToken = jwtTokenService.retrieveToken(refreshTokenHeader);

            // Refresh Token이 header로 전달되었을 경우, 해당 요청은 Access Token 재발급을 시도하는 요청이다.
            if (refreshToken != null) {
                log.info("[Log] Token reissue request");
                log.info("[Log] Refresh Token = {}", refreshToken);
                Authentication refreshAuthentication = authenticated(null, null, null);
                SecurityContextHolder.getContext().setAuthentication(refreshAuthentication);
            } else {
                throw e;
            }
        }
    }
}

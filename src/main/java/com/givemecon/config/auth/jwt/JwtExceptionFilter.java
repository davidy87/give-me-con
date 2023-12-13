package com.givemecon.config.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.error.ErrorResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            log.info("Access Token 만료");

            ErrorResponse errorResponse = ErrorResponse.builder()
                    .code(ErrorCode.TOKEN_EXPIRED.getCode())
                    .status(ErrorCode.TOKEN_EXPIRED.getStatus())
                    .message(ErrorCode.TOKEN_EXPIRED.getMessage())
                    .build();

            response.setStatus(errorResponse.getStatus());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        }
    }
}

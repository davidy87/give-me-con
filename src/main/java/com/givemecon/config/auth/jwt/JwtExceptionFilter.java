package com.givemecon.config.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.util.error.response.ErrorResponse;
import com.givemecon.util.exception.concrete.InvalidTokenException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

import static com.givemecon.util.error.ErrorCode.*;

@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        ErrorResponse errorResponse = null;

        try {
            log.info("--- In JwtExceptionFilter ---");
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            errorResponse = new ErrorResponse(ACCESS_TOKEN_EXPIRED);
        } catch (InvalidTokenException e) {
            errorResponse = new ErrorResponse(e.getErrorCode());
        } finally {
            if (errorResponse != null) {
                response.setStatus(errorResponse.getStatus());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("error", errorResponse)));
            }
        }
    }
}

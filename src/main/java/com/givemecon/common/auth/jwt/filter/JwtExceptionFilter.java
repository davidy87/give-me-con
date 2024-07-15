package com.givemecon.common.auth.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.common.error.ErrorCode;
import com.givemecon.common.error.response.ErrorResponse;
import com.givemecon.common.exception.GivemeconException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

import static com.givemecon.common.error.GlobalErrorCode.ACCESS_TOKEN_EXPIRED;
import static com.givemecon.common.error.GlobalErrorCode.TOKEN_NOT_AUTHENTICATED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public final class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            log.info("[Log] --- Begin JwtExceptionFilter ---");
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.info("[Log] Caught ExpiredJwtException", e);
            respondWithError(response, ACCESS_TOKEN_EXPIRED);
        } catch (JwtException e) {
            log.info("[Log] Caught JwtException", e);
            respondWithError(response, TOKEN_NOT_AUTHENTICATED);
        } catch (GivemeconException e) {
            log.info("[Log] Caught GivemeconException", e);
            respondWithError(response, e.getErrorCode());
        } finally {
            log.info("[Log] --- End JwtExceptionFilter ---");
        }
    }

    private void respondWithError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        response.setStatus(errorResponse.getStatus());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("error", errorResponse)));
    }
}

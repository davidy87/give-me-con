package com.givemecon.common.auth.jwt.exception;

import com.givemecon.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ErrorCode {

    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "ACCESS_TOKEN_EXPIRED", "Access Token의 유효기간이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "REFRESH_TOKEN_EXPIRED", "Refresh Token의 유효기간이 만료되었습니다."),
    TOKEN_NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED.value(), "TOKEN_NOT_AUTHENTICATED", "인증되지 않은 토큰입니다."),
    INVALID_AUTHORIZATION_CODE(HttpStatus.UNAUTHORIZED.value(), "INVALID_AUTHORIZATION_CODE", "올바르지 않은 Authorization Code입니다.");

    private final int status;

    private final String code;

    private final String message;
}

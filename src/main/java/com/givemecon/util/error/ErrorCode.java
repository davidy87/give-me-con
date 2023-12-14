package com.givemecon.util.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "001", "Entity Not Found (존재하지 않는 Entity입니다)."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "002", "Access Token Expired (기간이 만료된 토큰입니다).");

    private final int status;
    private final String code;
    private final String message;
}

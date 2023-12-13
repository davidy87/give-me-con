package com.givemecon.util.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_FOUND("001", HttpStatus.NOT_FOUND, "Entity Not Found (존재하지 않는 Entity입니다)."),
    TOKEN_EXPIRED("002", HttpStatus.FORBIDDEN, "Access Token Expired (기간이 만료된 토큰입니다).");

    private String code;
    private HttpStatus status;
    private String message;
}

package com.givemecon.util.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "001", "Entity Not Found (존재하지 않는 Entity입니다)."),
    NOT_VALID_ARGUMENT(HttpStatus.BAD_REQUEST.value(), "002", "Arguements Not Validated."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "003", "Access Token Expired (Access Token의 유효기간이 만료되었습니다)."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "004", "Refresh Token Expired (Refresh Token의 유효기간이 만료되었습니다).");

    private final int status;
    private final String code;
    private final String message;
}

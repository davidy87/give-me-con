package com.givemecon.util.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "001", "Entity Not Found (존재하지 않는 Entity입니다)."),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST.value(), "002", "Arguments Not Validated (데이터 검증에 실패했습니다)."),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST.value(), "002", "Missing Request Parameter (요청 파라미터가 전달되지 않았습니다)."),
    IMAGE_PROCESS_FAILED(HttpStatus.BAD_REQUEST.value(), "003", "Image Uploading Failed (이미지 파일 업로드에 실패하였습니다. 나중에 다시 시도해주세요.)"),

    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "004", "Access Token Expired (Access Token의 유효기간이 만료되었습니다)."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "005", "Refresh Token Expired (Refresh Token의 유효기간이 만료되었습니다)."),
    TOKEN_NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED.value(), "006", "Token Not Authenticated (인증되지 않은 토큰입니다)."),
    INVALID_AUTHORIZATION_CODE(HttpStatus.UNAUTHORIZED.value(), "007", "올바르지 않은 Authorization Code입니다.");

    private final int status;
    private final String code;
    private final String message;
}

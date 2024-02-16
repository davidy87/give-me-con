package com.givemecon.util.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "001", "Entity Not Found (존재하지 않는 Entity입니다)."),
    NOT_VALID_ARGUMENT(HttpStatus.BAD_REQUEST.value(), "002", "Arguments Not Validated (데이터 검증에 실패했습니다)."),
    IMAGE_PROCESS_FAILED(HttpStatus.BAD_REQUEST.value(), "003", "Image Uploading Failed (이미지 파일 업로드에 실패하였습니다. 나중에 다시 시도해주세요.)"),

    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "004", "Access Token Expired (Access Token의 유효기간이 만료되었습니다)."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "005", "Refresh Token Expired (Refresh Token의 유효기간이 만료되었습니다).");

    private final int status;
    private final String code;
    private final String message;
}

package com.givemecon.application.exception.errorcode;

import com.givemecon.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BrandErrorCode implements ErrorCode {

    INVALID_BRAND_ID(HttpStatus.BAD_REQUEST.value(), "INVALID_BRAND_ID", "Brand id가 올바르지 않습니다."),
    INVALID_BRAND_NAME(HttpStatus.BAD_REQUEST.value(), "INVALID_BRAND_NAME", "Brand name이 올바르지 않습니다."),
    CATEGORY_NOT_EXIST(HttpStatus.BAD_REQUEST.value(), "CATEGORY_NOT_EXIST", "Brand의 Category가 ");

    private final int status;

    private final String code;

    private final String message;
}

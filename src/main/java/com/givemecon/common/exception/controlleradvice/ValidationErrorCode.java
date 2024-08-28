package com.givemecon.common.exception.controlleradvice;

import com.givemecon.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ValidationErrorCode implements ErrorCode {

    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST.value(), "INVALID_ARGUMENT", "데이터 검증에 실패했습니다."),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST.value(), "MISSING_REQUEST_PARAMETER", "요청 파라미터가 전달되지 않았습니다.");

    private final int status;

    private final String code;

    private final String message;
}

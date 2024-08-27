package com.givemecon.application.exception.errorcode;

import com.givemecon.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum VoucherKindErrorCode implements ErrorCode {

    INVALID_VOUCHER_KIND_ID(HttpStatus.BAD_REQUEST.value(), "INVALID_VOUCHER_KIND_ID", "Category id가 올바르지 않습니다.");

    private final int status;

    private final String code;

    private final String message;
}

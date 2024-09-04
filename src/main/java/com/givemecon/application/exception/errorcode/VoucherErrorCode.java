package com.givemecon.application.exception.errorcode;

import com.givemecon.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum VoucherErrorCode implements ErrorCode {

    INVALID_VOUCHER_ID(HttpStatus.BAD_REQUEST.value(), "INVALID_VOUCHER_ID", "Voucher id가 올바르지 않습니다."),
    VOUCHER_NOT_FOR_SALE(HttpStatus.BAD_REQUEST.value(), "VOUCHER_NOT_FOR_SALE", "판매 중인 voucher가 아닙니다"),
    INVALID_STATUS_CODE(HttpStatus.BAD_REQUEST.value(), "INVALID_STATUS_CODE", "상태 코드가 올바르지 않습니다.");

    private final int status;

    private final String code;

    private final String message;
}

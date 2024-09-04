package com.givemecon.application.exception.errorcode;

import com.givemecon.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PurchasedVoucherErrorCode implements ErrorCode {

    INVALID_PURCHASED_VOUCHER_ID(HttpStatus.BAD_REQUEST.value(), "INVALID_PURCHASED_VOUCHER_ID", "PurchasedVoucher id가 올바르지 않습니다."),
    PURCHASED_VOUCHER_NOT_USABLE(HttpStatus.BAD_REQUEST.value(), "PURCHASED_VOUCHER_NOT_USABLE", "PurchasedVoucher의 상태가 USABLE이 아닙니다.");

    private final int status;

    private final String code;

    private final String message;
}

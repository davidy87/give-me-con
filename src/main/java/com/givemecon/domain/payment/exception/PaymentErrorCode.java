package com.givemecon.domain.payment.exception;

import com.givemecon.util.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    AMOUNT_NOT_MATCH(HttpStatus.BAD_REQUEST.value(), "AMOUNT_NOT_MATCH", "결제할 가격이 주문금액과 일치하지 않습니다.");

    private final int status;

    private final String code;

    private final String message;
}

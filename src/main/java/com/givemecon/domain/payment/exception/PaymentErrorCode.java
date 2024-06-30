package com.givemecon.domain.payment.exception;

import com.givemecon.util.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    CUSTOMER_NOT_MATCH(HttpStatus.BAD_REQUEST.value(), "BUYER_NOT_MATCH", "구매자와 결제 요청자가 일치하지 않습니다."),
    ORDER_NOT_IN_PROGRESS(HttpStatus.BAD_REQUEST.value(), "ORDER_NOT_IN_PROGRESS", "진행 중이지 않은 주문입니다. 결제를 더 이상 진행할 수 없습니다."),
    AMOUNT_NOT_MATCH(HttpStatus.BAD_REQUEST.value(), "AMOUNT_NOT_MATCH", "결제할 가격이 주문금액과 일치하지 않습니다.");

    private final int status;

    private final String code;

    private final String message;
}

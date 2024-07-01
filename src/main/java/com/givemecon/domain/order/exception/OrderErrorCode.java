package com.givemecon.domain.order.exception;

import com.givemecon.util.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ITEM_NOT_FOR_SALE(HttpStatus.BAD_REQUEST.value(), "ITEM_NOT_FOR_SALE", "판매 중이 아닌 기프티콘입니다."),
    ITEM_ORDER_NOT_PLACED(HttpStatus.BAD_REQUEST.value(), "ITEM_ORDER_NOT_PLACED", "주문 중이 아닌 기프티콘입니다."),
    SELLER_UNAVAILABLE(HttpStatus.BAD_REQUEST.value(), "SELLER_UNAVAILABLE", "판매자가 존재하지 않습니다."),
    BUYER_NOT_MATCH(HttpStatus.BAD_REQUEST.value(), "BUYER_NOT_MATCH", "현재 회원 정보가 주문 정보와 일치하지 않습니다."),
    BUYER_EQUALS_SELLER(HttpStatus.BAD_REQUEST.value(), "BUYER_EQUALS_SELLER", "구매자가 기프티콘의 판매자와 같습니다."),
    ORDER_ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST.value(), "ORDER_ALREADY_CONFIRMED", "이미 체결된 주문입니다."),
    ORDER_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST.value(), "ORDER_ALREADY_CANCELLED", "이미 취소된 주문입니다.");

    private final int status;

    private final String code;

    private final String message;
}

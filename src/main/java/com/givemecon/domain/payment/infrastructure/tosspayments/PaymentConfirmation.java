package com.givemecon.domain.payment.infrastructure.tosspayments;

import com.givemecon.domain.payment.entity.Payment;
import com.givemecon.domain.payment.dto.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class PaymentConfirmation {

    private String paymentKey;

    private String status;

    private String orderId;

    private String orderName;

    private Long totalAmount;

    private Map<String, String> receipt;

    public Payment toEntity() {
        return Payment.builder()
                .paymentKey(paymentKey)
                .method(PaymentMethod.CARD)
                .orderId(orderId)
                .orderName(orderName)
                .amount(totalAmount)
                .receiptUrl(receipt.get("url"))
                .build();
    }
}

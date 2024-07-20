package com.givemecon.infrastructure.tosspayments;

import com.givemecon.domain.entity.payment.OrderInfo;
import com.givemecon.domain.entity.payment.Payment;
import com.givemecon.domain.entity.payment.PaymentMethod;
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
                .orderInfo(new OrderInfo(orderId, orderName, totalAmount))
                .receiptUrl(receipt.get("url"))
                .build();
    }
}

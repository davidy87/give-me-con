package com.givemecon.domain.payment;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentRequest {

        private String paymentKey;

        private String orderId;

        private Long amount;
    }

    @Getter
    @NoArgsConstructor
    public static class PaymentResponse {

        private Long amount;

        private String orderId;

        private String orderName;

        private String receiptUrl;

        @Builder
        public PaymentResponse(Payment payment) {
            this.amount = payment.getAmount();
            this.orderId = payment.getOrderId();
            this.orderName = payment.getOrderName();
            this.receiptUrl = payment.getReceiptUrl();
        }
    }
}

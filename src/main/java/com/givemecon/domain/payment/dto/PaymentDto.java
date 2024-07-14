package com.givemecon.domain.payment.dto;

import com.givemecon.domain.payment.entity.Payment;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentRequest {

        @NotBlank
        private String paymentKey;

        @NotBlank
        private String orderId;

        @Min(0L)
        @NotNull
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

package com.givemecon.application.dto;

import com.givemecon.domain.entity.order.OrderStatus;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucher.VoucherStatus;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderRequest {

        @NotEmpty
        private List<@Min(1L) Long> voucherIdList;
    }

    @Getter
    @NoArgsConstructor
    public static class OrderNumberResponse {

        private String orderNumber;

        public OrderNumberResponse(String orderNumber) {
            this.orderNumber = orderNumber;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderSummary {

        private String orderNumber;

        private OrderStatus status;

        private String customerName;

        private Integer quantity;

        private Long totalPrice;

        private List<OrderItem> orderItems;
    }

    @Getter
    @NoArgsConstructor
    public static class OrderItem {

        private Long voucherId;

        private Long price;

        private String brandName;

        private String title;

        private String voucherImageUrl;

        private LocalDate expDate;

        private VoucherStatus status;

        public OrderItem(Voucher voucher) {
            VoucherKind voucherKind = voucher.getVoucherKind();

            this.voucherId = voucher.getId();
            this.price = voucher.getPrice();
            this.brandName = voucherKind.getBrand().getName();
            this.title = voucher.getTitle();
            this.voucherImageUrl = voucherKind.getImageUrl();
            this.expDate = voucher.getExpDate();
            this.status = voucher.getStatus();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderConfirmation {

        private Long amount;
    }
}

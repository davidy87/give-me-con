package com.givemecon.domain.order.dto;

import com.givemecon.domain.voucherkind.entity.VoucherKind;
import com.givemecon.domain.voucher.entity.Voucher;
import com.givemecon.domain.voucher.dto.VoucherStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class OrderDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderRequest {

        @NotEmpty
        private List<@Min(1L) Long> voucherForSaleIdList;
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

        private Long voucherForSaleId;

        private Long price;

        private String brandName;

        private String title;

        private String voucherImageUrl;

        private LocalDate expDate;

        private VoucherStatus status;

        public OrderItem(Voucher voucher) {
            VoucherKind voucherKind = voucher.getVoucherKind();

            this.voucherForSaleId = voucher.getId();
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

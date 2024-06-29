package com.givemecon.domain.order;

import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleStatus;
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

        private VoucherForSaleStatus status;

        public OrderItem(VoucherForSale voucherForSale) {
            Voucher voucher = voucherForSale.getVoucher();

            this.voucherForSaleId = voucherForSale.getId();
            this.price = voucherForSale.getPrice();
            this.brandName = voucher.getBrand().getName();
            this.title = voucherForSale.getTitle();
            this.voucherImageUrl = voucher.getImageUrl();
            this.expDate = voucherForSale.getExpDate();
            this.status = voucherForSale.getStatus();
        }
    }
}

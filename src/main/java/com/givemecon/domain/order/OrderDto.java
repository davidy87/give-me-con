package com.givemecon.domain.order;

import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class OrderDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderRequest {

        private List<Long> voucherForSaleIdList;
    }

    @Getter
    @NoArgsConstructor
    public static class OrderNumberResponse {

        private Long orderNumber;

        public OrderNumberResponse(Long orderNumber) {
            this.orderNumber = orderNumber;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class OrderSummary {

        private OrderStatus status;

        private Integer quantity;

        private Long totalPrice;

        private List<OrderItem> orderItems;

        public OrderSummary(OrderStatus status, Integer quantity, Long totalPrice, List<OrderItem> orderItems) {
            this.status = status;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
            this.orderItems = orderItems;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class OrderItem {

        private Long voucherForSaleId;

        private Long price;

        private String title;

        private String imageUrl;

        private LocalDate expDate;

        private VoucherForSaleStatus status;

        public OrderItem(VoucherForSale voucherForSale) {
            this.voucherForSaleId = voucherForSale.getId();
            this.price = voucherForSale.getPrice();
            this.title = voucherForSale.getTitle();
            this.imageUrl = voucherForSale.getImageUrl();
            this.expDate = voucherForSale.getExpDate();
            this.status = voucherForSale.getStatus();
        }
    }
}

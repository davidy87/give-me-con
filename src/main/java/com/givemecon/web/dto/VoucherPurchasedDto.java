package com.givemecon.web.dto;

import com.givemecon.domain.voucherpurchased.VoucherPurchased;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class VoucherPurchasedDto {

    @Getter
    @Builder
    public static class VoucherPurchasedRequest {

        private String title;

        private String image;

        private Long price;

        private LocalDate expDate;

        private String barcode;

        private Long voucherId;

        public VoucherPurchased toEntity() {
            return VoucherPurchased.builder()
                    .image(image)
                    .title(title)
                    .price(price)
                    .expDate(expDate)
                    .barcode(barcode)
                    .build();
        }
    }

    @Getter
    public static class VoucherPurchasedResponse {

        private Long id;

        private String image;

        private String title;

        private Long price;

        private LocalDate expDate;

        private String barcode;

        public VoucherPurchasedResponse(VoucherPurchased voucherPurchased) {
            this.id = voucherPurchased.getId();
            this.title = voucherPurchased.getTitle();
            this.price = voucherPurchased.getPrice();
            this.expDate = voucherPurchased.getExpDate();
            this.barcode = voucherPurchased.getBarcode();
            this.image = voucherPurchased.getImage();
        }
    }
}

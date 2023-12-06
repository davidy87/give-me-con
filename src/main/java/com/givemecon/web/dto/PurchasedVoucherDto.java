package com.givemecon.web.dto;

import com.givemecon.domain.purchasedvoucher.PurchasedVoucher;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class PurchasedVoucherDto {

    @Getter
    @Builder
    public static class PurchasedVoucherRequest {

        private String title;

        private String image;

        private Long price;

        private LocalDate expDate;

        private String barcode;

        private Long voucherId;

        public PurchasedVoucher toEntity() {
            return PurchasedVoucher.builder()
                    .image(image)
                    .title(title)
                    .price(price)
                    .expDate(expDate)
                    .barcode(barcode)
                    .build();
        }
    }

    @Getter
    public static class PurchasedVoucherResponse {

        private Long id;

        private String image;

        private String title;

        private Long price;

        private LocalDate expDate;

        private String barcode;

        public PurchasedVoucherResponse(PurchasedVoucher purchasedVoucher) {
            this.id = purchasedVoucher.getId();
            this.title = purchasedVoucher.getTitle();
            this.price = purchasedVoucher.getPrice();
            this.expDate = purchasedVoucher.getExpDate();
            this.barcode = purchasedVoucher.getBarcode();
            this.image = purchasedVoucher.getImage();
        }
    }
}

package com.givemecon.web.dto;

import com.givemecon.domain.purchasedvoucher.PurchasedVoucher;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class PurchasedVoucherDto {

    @Getter
    @Builder
    public static class PurchasedVoucherRequest {

        private final String title;

        private final String image;

        private final Long price;

        private final LocalDate expDate;

        private final String barcode;

        private final Long voucherId;

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

        private final Long id;

        private final String image;

        private final String title;

        private final Long price;

        private final LocalDate expDate;

        private final String barcode;

        private final Boolean valid;

        public PurchasedVoucherResponse(PurchasedVoucher purchasedVoucher) {
            this.id = purchasedVoucher.getId();
            this.title = purchasedVoucher.getTitle();
            this.price = purchasedVoucher.getPrice();
            this.expDate = purchasedVoucher.getExpDate();
            this.barcode = purchasedVoucher.getBarcode();
            this.image = purchasedVoucher.getImage();
            this.valid = purchasedVoucher.getValid();
        }
    }
}

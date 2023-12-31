package com.givemecon.web.dto;

import com.givemecon.domain.voucher.VoucherForSale;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class VoucherForSaleDto {

    @Getter
    @Builder
    public static class VoucherForSaleRequest {

        private String title;

        private Long price;

        private LocalDate expDate;

        private String barcode;

        private String image;

        public VoucherForSale toEntity() {
            return VoucherForSale.builder()
                    .title(title)
                    .price(price)
                    .expDate(expDate)
                    .barcode(barcode)
                    .image(image)
                    .build();
        }
    }

    @Getter
    public static class VoucherForSaleResponse {

        private Long id;

        private String title;

        private Long price;

        private LocalDate expDate;

        private String barcode;

        private String image;


        public VoucherForSaleResponse(VoucherForSale voucherForSale) {
            this.id = voucherForSale.getId();
            this.title = voucherForSale.getTitle();
            this.price = voucherForSale.getPrice();
            this.expDate = voucherForSale.getExpDate();
            this.barcode = voucherForSale.getBarcode();
            this.image = voucherForSale.getImage();
        }
    }
}

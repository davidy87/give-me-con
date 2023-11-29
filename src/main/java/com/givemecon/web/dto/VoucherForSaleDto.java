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
}

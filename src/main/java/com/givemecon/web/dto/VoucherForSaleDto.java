package com.givemecon.web.dto;

import com.givemecon.domain.voucherforsale.VoucherForSale;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class VoucherForSaleDto {

    @Getter
    @Builder
    public static class VoucherForSaleRequest {

        private final String title;

        private final Long price;

        private final LocalDate expDate;

        private final String barcode;

        private final MultipartFile imageFile;

        public VoucherForSale toEntity() {
            return VoucherForSale.builder()
                    .title(title)
                    .price(price)
                    .expDate(expDate)
                    .barcode(barcode)
                    .build();
        }
    }

    @Getter
    public static class VoucherForSaleResponse {

        private final Long id;

        private final String title;

        private final Long price;

        private final LocalDate expDate;

        private final String barcode;

        private final String image;


        public VoucherForSaleResponse(VoucherForSale voucherForSale) {
            this.id = voucherForSale.getId();
            this.title = voucherForSale.getTitle();
            this.price = voucherForSale.getPrice();
            this.expDate = voucherForSale.getExpDate();
            this.barcode = voucherForSale.getBarcode();
            this.image = voucherForSale.getVoucherForSaleImage().getImageUrl();
        }
    }
}

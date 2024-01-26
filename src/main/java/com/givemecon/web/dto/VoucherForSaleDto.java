package com.givemecon.web.dto;

import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.util.validator.ValidFile;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class VoucherForSaleDto {

    @Getter
    @Builder
    public static class VoucherForSaleRequest {

        @NotBlank
        private final String title;

        @NotNull
        @Min(0)
        private final Long price;

        @NotNull
        @Future
        private final LocalDate expDate;

        @NotBlank
        private final String barcode;

        @ValidFile
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

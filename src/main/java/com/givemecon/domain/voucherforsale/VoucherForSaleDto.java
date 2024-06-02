package com.givemecon.domain.voucherforsale;

import com.givemecon.util.validator.ValidFile;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VoucherForSaleDto {

    @Getter
    @RequiredArgsConstructor
    public static class VoucherForSaleRequest {

        @NotNull
        @Min(1L)
        private final Long voucherId;

        @NotNull
        @Min(0L)
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
                    .price(price)
                    .expDate(expDate)
                    .barcode(barcode)
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class StatusCodeParameter {

        @Min(0)
        @Max(4)
        private final Integer statusCode;
    }

    @Getter
    @Setter
    public static class StatusCodeBody {

        @Min(0)
        @Max(4)
        @NotNull
        private Integer statusCode;
    }

    @Getter
    public static class VoucherForSaleResponse {

        private final Long id;

        private final String title;

        private final Long price;

        private final LocalDate expDate;

        private final String barcode;

        private final String imageUrl;

        private final VoucherForSaleStatus status;

        public VoucherForSaleResponse(VoucherForSale voucherForSale) {
            this.id = voucherForSale.getId();
            this.title = voucherForSale.getTitle();
            this.price = voucherForSale.getPrice();
            this.expDate = voucherForSale.getExpDate();
            this.barcode = voucherForSale.getBarcode();
            this.imageUrl = voucherForSale.getImageUrl();
            this.status = voucherForSale.getStatus();
        }
    }
}

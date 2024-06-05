package com.givemecon.domain.voucherforsale;

import com.givemecon.util.validator.ValidFile;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;

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
    public static class StatusUpdateRequest {

        @Min(0)
        @Max(4)
        @NotNull
        private Integer statusCode;

        private String rejectedReason;

        @AssertTrue(message = "판매 거절 시, 거절 사유는 필수입니다.")
        private boolean isRejectedReason() {
            boolean valid = StringUtils.hasText(rejectedReason);

            if (statusCode == REJECTED.ordinal()) {
                return valid;
            }

            return !valid;
        }
    }

    @Getter
    public static class VoucherForSaleResponse {

        private final Long id;

        private final Long price;

        private final String title;

        private final String barcode;

        private final String imageUrl;

        private final LocalDate expDate;

        private final VoucherForSaleStatus status;

        private final LocalDate saleRequestedDate;

        public VoucherForSaleResponse(VoucherForSale voucherForSale) {
            this.id = voucherForSale.getId();
            this.price = voucherForSale.getPrice();
            this.title = voucherForSale.getTitle();
            this.barcode = voucherForSale.getBarcode();
            this.imageUrl = voucherForSale.getImageUrl();
            this.expDate = voucherForSale.getExpDate();
            this.status = voucherForSale.getStatus();
            this.saleRequestedDate = voucherForSale.getSaleRequestedDate();
        }
    }
}

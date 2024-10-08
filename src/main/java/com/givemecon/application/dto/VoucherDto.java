package com.givemecon.application.dto;

import com.givemecon.application.dto.validator.ValidFile;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucher.VoucherStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

import static com.givemecon.domain.entity.voucher.VoucherStatus.SALE_REJECTED;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VoucherDto {

    @Getter
    @RequiredArgsConstructor
    public static class VoucherRequest {

        @NotNull
        @Min(1L)
        private final Long voucherKindId;

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
    }

    @Getter
    @RequiredArgsConstructor
    public static class QueryParameter {

        @Min(1)
        private final Long voucherKindId;

        @Min(0)
        @Max(5)
        private final Integer statusCode;
    }

    @Getter
    @Setter
    public static class StatusUpdateRequest {

        @Min(0)
        @Max(5)
        @NotNull
        private Integer statusCode;

        private String rejectedReason;

        @AssertTrue(message = "판매 거절 시, 거절 사유는 필수입니다.")
        private boolean isRejectedReason() {
            boolean valid = StringUtils.hasText(rejectedReason);

            if (statusCode == SALE_REJECTED.ordinal()) {
                return valid;
            }

            return !valid;
        }
    }

    @Getter
    public static class VoucherResponse {

        private final Long id;

        private final Long price;

        private final String title;

        private final String barcode;

        private final LocalDate expDate;

        private final VoucherStatus status;

        private final LocalDate saleRequestedDate;

        public VoucherResponse(Voucher voucher) {
            this.id = voucher.getId();
            this.price = voucher.getPrice();
            this.title = voucher.getTitle();
            this.barcode = voucher.getBarcode();
            this.expDate = voucher.getExpDate();
            this.status = voucher.getStatus();
            this.saleRequestedDate = voucher.getSaleRequestedDate();
        }
    }

    @Getter
    public static class ImageResponse {

        private final String imageUrl;

        public ImageResponse(Voucher voucher) {
            this.imageUrl = voucher.getImageUrl();
        }
    }
}

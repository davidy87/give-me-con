package com.givemecon.domain.voucher;

import com.givemecon.util.validator.ValidFile;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VoucherDto {

    @Getter
    @Builder
    public static class VoucherSaveRequest {

        @NotNull
        @Min(1L)
        private final Long categoryId;

        @NotNull
        @Min(1L)
        private final Long brandId;

        @NotNull
        @Min(0L)
        private final Long price;

        @NotBlank
        private final String title;

        @ValidFile
        private final MultipartFile imageFile;

        public Voucher toEntity() {
            return Voucher.builder()
                    .price(price)
                    .title(title)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class VoucherUpdateRequest {

        private final String title;

        private final String description;

        private final String caution;

        private final MultipartFile imageFile;
    }

    @Getter
    public static class VoucherResponse {

        private final Long id;

        private final Long minPrice;

        private final String title;

        private final String imageUrl;

        public VoucherResponse(Voucher voucher) {
            this.id = voucher.getId();
            this.minPrice = voucher.getPrice();
            this.title = voucher.getTitle();
            this.imageUrl = voucher.getImageUrl();
        }
    }

    @Getter
    public static class PagedVoucherResponse {

        private final int number;

        private final int totalPages;

        private final int size;

        private final List<VoucherResponse> vouchers;

        public PagedVoucherResponse(Page<VoucherResponse> pageResult) {
            this.number = pageResult.getNumber();
            this.totalPages = pageResult.getTotalPages();
            this.size = pageResult.getSize();
            this.vouchers = pageResult.getContent();
        }
    }
}

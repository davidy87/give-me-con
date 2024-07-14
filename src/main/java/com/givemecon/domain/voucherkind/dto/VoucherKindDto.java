package com.givemecon.domain.voucherkind.dto;

import com.givemecon.domain.voucherkind.entity.VoucherKind;
import com.givemecon.util.validator.ValidFile;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VoucherKindDto {

    @Getter
    @RequiredArgsConstructor
    public static class VoucherKindSaveRequest {

        @NotNull
        @Min(1L)
        private final Long brandId;

        @NotBlank
        private final String title;

        private final String description;

        private final String caution;

        @ValidFile
        private final MultipartFile imageFile;

        public VoucherKind toEntity() {
            return VoucherKind.builder()
                    .title(title)
                    .description(description)
                    .caution(caution)
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class VoucherKindUpdateRequest {

        private final String title;

        private final String description;

        private final String caution;

        private final MultipartFile imageFile;
    }

    @Getter
    public static class VoucherKindResponse {

        private final Long id;

        private final Long minPrice;

        private final String title;

        private final String imageUrl;

        private final String description;

        private final String caution;

        public VoucherKindResponse(VoucherKind voucherKind) {
            this.id = voucherKind.getId();
            this.minPrice = 0L;
            this.title = voucherKind.getTitle();
            this.imageUrl = voucherKind.getImageUrl();
            this.description = voucherKind.getDescription();
            this.caution = voucherKind.getCaution();
        }

        public VoucherKindResponse(VoucherKind voucherKind, Long minPrice) {
            this.id = voucherKind.getId();
            this.minPrice = minPrice;
            this.title = voucherKind.getTitle();
            this.imageUrl = voucherKind.getImageUrl();
            this.description = voucherKind.getDescription();
            this.caution = voucherKind.getCaution();
        }
    }

    @Getter
    public static class PagedVoucherKindResponse {

        private final int number;

        private final int totalPages;

        private final int size;

        private final List<VoucherKindResponse> vouchers;

        public PagedVoucherKindResponse(Page<VoucherKindResponse> pageResult) {
            this.number = pageResult.getNumber();
            this.totalPages = pageResult.getTotalPages();
            this.size = pageResult.getSize();
            this.vouchers = pageResult.getContent();
        }
    }
}
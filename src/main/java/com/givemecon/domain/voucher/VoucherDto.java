package com.givemecon.domain.voucher;

import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.util.validator.ValidFile;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.FOR_SALE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VoucherDto {

    @Getter
    @RequiredArgsConstructor
    public static class VoucherSaveRequest {

        @NotNull
        @Min(1L)
        private final Long brandId;

        @NotBlank
        private final String title;

        private final String description;

        private final String caution;

        @ValidFile
        private final MultipartFile imageFile;

        public Voucher toEntity() {
            return Voucher.builder()
                    .title(title)
                    .description(description)
                    .caution(caution)
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor
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

        private final String description;

        private final String caution;

        public VoucherResponse(Voucher voucher) {
            this.id = voucher.getId();
            this.title = voucher.getTitle();
            this.imageUrl = voucher.getImageUrl();
            this.description = voucher.getDescription();
            this.caution = voucher.getCaution();
            this.minPrice = voucher.getVoucherForSaleList().stream()
                    .filter(voucherForSale -> voucherForSale.getStatus() == FOR_SALE)
                    .map(VoucherForSale::getPrice)
                    .reduce(Long::min)
                    .orElse(0L);
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

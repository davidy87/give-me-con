package com.givemecon.web.dto;

import com.givemecon.domain.voucher.Voucher;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class VoucherDto {

    @Getter
    @Builder
    public static class VoucherSaveRequest {

        private final Long price;

        private final String title;

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

        private final Long price;

        private final String title;

        private final String image;

        public VoucherResponse(Voucher voucher) {
            this.id = voucher.getId();
            this.price = voucher.getPrice();
            this.title = voucher.getTitle();
            this.image = voucher.getVoucherImage().getImageUrl();
        }
    }
}

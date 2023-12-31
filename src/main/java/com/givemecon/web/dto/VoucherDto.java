package com.givemecon.web.dto;

import com.givemecon.domain.voucher.Voucher;
import lombok.Builder;
import lombok.Getter;

public class VoucherDto {

    @Getter
    @Builder
    public static class VoucherSaveRequest {

        private Long price;

        private String title;

        private String image;

        public Voucher toEntity() {
            return Voucher.builder()
                    .price(price)
                    .title(title)
                    .image(image)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class VoucherUpdateRequest {

        private Long price;

        private String image;
    }

    @Getter
    public static class VoucherResponse {

        private Long id;

        private Long price;

        private String title;

        private String image;

        public VoucherResponse(Voucher voucher) {
            this.id = voucher.getId();
            this.price = voucher.getPrice();
            this.title = voucher.getTitle();
            this.image = voucher.getImage();
        }
    }
}

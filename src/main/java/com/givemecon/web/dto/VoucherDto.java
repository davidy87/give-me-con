package com.givemecon.web.dto;

import com.givemecon.domain.voucher.Voucher;
import lombok.Builder;
import lombok.Getter;

public class VoucherDto {

    @Getter
    @Builder
    public static class VoucherSaveRequest {

        private Long price;

        private String image;

        public Voucher toEntity() {
            return Voucher.builder()
                    .price(price)
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
    @Builder
    public static class VoucherResponse {

        private Long id;

        private Long price;

        private String image;
    }
}

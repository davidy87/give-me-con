package com.givemecon.web.dto;

import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherSelling;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

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
    @Builder
    public static class VoucherResponse {

        private Long id;

        private Long price;

        private String title;

        private String image;
    }

    @Getter
    public static class VoucherSellingResponse {

        private Long id;

        private Long price;

        private String title;

        private String image;

        private LocalDate expDate;

        public VoucherSellingResponse(VoucherSelling voucherSelling) {
            this.id = voucherSelling.getId();
            this.price = voucherSelling.getPrice();
            this.title = voucherSelling.getTitle();
            this.image = voucherSelling.getImage();
            this.expDate = voucherSelling.getExpDate();
        }
    }
}

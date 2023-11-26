package com.givemecon.web.dto;

import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherForSale;
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

    @Getter
    public static class VoucherForSaleResponse {

        private Long id;

        private Long price;

        private String title;

        private String image;

        private LocalDate expDate;

        public VoucherForSaleResponse(VoucherForSale voucherSelling) {
            this.id = voucherSelling.getId();
            this.price = voucherSelling.getPrice();
            this.title = voucherSelling.getTitle();
            this.image = voucherSelling.getImage();
            this.expDate = voucherSelling.getExpDate();
        }
    }
}

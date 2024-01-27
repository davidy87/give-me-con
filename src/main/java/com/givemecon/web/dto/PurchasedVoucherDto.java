package com.givemecon.web.dto;

import com.givemecon.domain.purchasedvoucher.PurchasedVoucher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PurchasedVoucherDto {

    @Getter
    @Builder
    public static class PurchasedVoucherRequest {

        @NotBlank
        private final String title;

        @NotBlank
        private final String image;

        @NotNull
        @Min(0)
        private final Long price;

        @NotNull
        @Future
        private final LocalDate expDate;

        @NotBlank
        private final String barcode;

        @NotNull
        @Min(1)
        private final Long voucherId;

        public PurchasedVoucher toEntity() {
            return PurchasedVoucher.builder()
                    .image(image)
                    .title(title)
                    .price(price)
                    .expDate(expDate)
                    .barcode(barcode)
                    .build();
        }
    }

    @Getter
    public static class PurchasedVoucherRequestList {

        @NotEmpty
        @Valid
        private final List<PurchasedVoucherRequest> requestList;

        public PurchasedVoucherRequestList() {
            requestList = new ArrayList<>();
        }

        public PurchasedVoucherRequestList(List<PurchasedVoucherRequest> requestList) {
            this.requestList = requestList;
        }
    }

    @Getter
    public static class PurchasedVoucherResponse {

        private final Long id;

        private final String image;

        private final String title;

        private final Long price;

        private final LocalDate expDate;

        private final String barcode;

        private final Boolean valid;

        public PurchasedVoucherResponse(PurchasedVoucher purchasedVoucher) {
            this.id = purchasedVoucher.getId();
            this.title = purchasedVoucher.getTitle();
            this.price = purchasedVoucher.getPrice();
            this.expDate = purchasedVoucher.getExpDate();
            this.barcode = purchasedVoucher.getBarcode();
            this.image = purchasedVoucher.getImage();
            this.valid = purchasedVoucher.getValid();
        }
    }
}

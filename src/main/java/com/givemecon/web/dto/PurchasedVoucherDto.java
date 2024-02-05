package com.givemecon.web.dto;

import com.givemecon.domain.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PurchasedVoucherDto {

    @Getter
    @NoArgsConstructor
    public static class PurchasedVoucherRequest {

        @NotNull
        @Min(1)
        private Long voucherForSaleId;

        public PurchasedVoucherRequest(Long voucherForSaleId) {
            this.voucherForSaleId = voucherForSaleId;
        }
    }

    @Getter
    public static class PurchasedVoucherRequestList {

        @NotEmpty
        @Valid
        private final List<PurchasedVoucherRequest> requests;

        public PurchasedVoucherRequestList() {
            requests = new ArrayList<>();
        }

        public PurchasedVoucherRequestList(List<PurchasedVoucherRequest> requests) {
            this.requests = requests;
        }
    }

    @Getter
    public static class PurchasedVoucherResponse {

        private final Long id;

        private final String title;

        private final Long price;

        private final LocalDate expDate;

        private final String barcode;

        private final String imageUrl;

        private final Boolean isValid;

        public PurchasedVoucherResponse(PurchasedVoucher purchasedVoucher) {
            VoucherForSale voucherForSale = purchasedVoucher.getVoucherForSale();

            this.id = purchasedVoucher.getId();
            this.title = voucherForSale.getTitle();
            this.price = voucherForSale.getPrice();
            this.expDate = voucherForSale.getExpDate();
            this.barcode = voucherForSale.getBarcode();
            this.imageUrl = voucherForSale.getVoucherForSaleImage().getImageUrl();
            this.isValid = purchasedVoucher.getIsValid();
        }
    }
}

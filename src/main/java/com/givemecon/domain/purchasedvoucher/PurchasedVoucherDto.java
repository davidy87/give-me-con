package com.givemecon.domain.purchasedvoucher;

import com.givemecon.domain.voucherforsale.VoucherForSale;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PurchasedVoucherDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchasedVoucherRequest {

        @NotNull
        @Min(1L)
        private Long voucherForSaleId;
    }

    @Getter
    @RequiredArgsConstructor
    public static class PurchasedVoucherRequestList {

        @NotEmpty
        @Valid
        private final List<PurchasedVoucherRequest> requests;

        public PurchasedVoucherRequestList() {
            requests = new ArrayList<>();
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
            this.imageUrl = voucherForSale.getImageUrl();
            this.isValid = purchasedVoucher.getIsValid();
        }
    }

    @Getter
    public static class PagedPurchasedVoucherResponse {

        private final int number;

        private final int totalPages;

        private final int size;

        private final List<PurchasedVoucherResponse> purchasedVouchers;

        public PagedPurchasedVoucherResponse(Page<PurchasedVoucherResponse> pageResult) {
            this.number = pageResult.getNumber();
            this.totalPages = pageResult.getTotalPages();
            this.size = pageResult.getSize();
            this.purchasedVouchers = pageResult.getContent();
        }
    }
}

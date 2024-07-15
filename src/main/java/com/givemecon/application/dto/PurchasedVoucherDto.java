package com.givemecon.application.dto;

import com.givemecon.domain.entity.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.entity.purchasedvoucher.PurchasedVoucherStatus;
import com.givemecon.domain.entity.voucher.Voucher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

        private final String voucherKindImageUrl;

        private final PurchasedVoucherStatus status;

        public PurchasedVoucherResponse(PurchasedVoucher purchasedVoucher) {
            Voucher voucher = purchasedVoucher.getVoucher();

            this.id = purchasedVoucher.getId();
            this.title = voucher.getTitle();
            this.price = voucher.getPrice();
            this.expDate = voucher.getExpDate();
            this.barcode = voucher.getBarcode();
            this.voucherKindImageUrl = voucher.getVoucherKind().getImageUrl();
            this.status = purchasedVoucher.getStatus();
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

    @Getter
    public static class StatusUpdateResponse {

        private final Long id;

        private final PurchasedVoucherStatus status;

        public StatusUpdateResponse(PurchasedVoucher purchasedVoucher) {
            this.id = purchasedVoucher.getId();
            this.status = purchasedVoucher.getStatus();
        }
    }
}

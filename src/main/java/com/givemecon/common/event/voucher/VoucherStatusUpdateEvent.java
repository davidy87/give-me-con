package com.givemecon.common.event.voucher;

import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucher.VoucherStatus;
import lombok.Getter;

@Getter
public class VoucherStatusUpdateEvent {

    private final String username;

    private final String voucherTitle;

    private final VoucherStatus newStatus;

    public VoucherStatusUpdateEvent(Voucher voucher) {
        this.username = voucher.getSeller().getUsername();
        this.voucherTitle = voucher.getTitle();
        this.newStatus = voucher.getStatus();
    }
}

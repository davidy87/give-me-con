package com.givemecon.event.voucher;

import com.givemecon.domain.entity.voucher.VoucherStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VoucherStatusUpdateEvent {

    private final String voucherTitle;

    private final VoucherStatus newStatus;
}

package com.givemecon.event.voucher;

import com.givemecon.domain.entity.voucher.VoucherStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class VoucherStatusUpdateEventListener {

    @EventListener
    public void listen(VoucherStatusUpdateEvent event) {
        String voucherTitle = event.getVoucherTitle();
        VoucherStatus newStatus = event.getNewStatus();
        String notification = "";

        switch (newStatus) {
            case SALE_REJECTED ->
                    notification = String.format("기프티콘 \"%s\"의 판매 요청이 거절되었습니다.", voucherTitle);

            case FOR_SALE ->
                    notification = String.format("기프티콘 \"%s\"의 판매 요청이 허가되었습니다.", voucherTitle);

            case SOLD ->
                    notification = String.format("기프티콘 \"%s\"의 판매가 완료되었습니다.", voucherTitle);
        }

        log.info("VoucherStatusUpdateEventListener = {}", notification);
    }
}

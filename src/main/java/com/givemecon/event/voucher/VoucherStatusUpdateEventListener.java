package com.givemecon.event.voucher;

import com.givemecon.domain.entity.voucher.VoucherStatus;
import com.givemecon.event.notification.service.NotificationService;
import com.givemecon.event.notification.service.exception.SseUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.givemecon.event.notification.util.EventType.VOUCHER_STATUS_UPDATE;

@Slf4j
@RequiredArgsConstructor
@Component
public class VoucherStatusUpdateEventListener {

    private final NotificationService notificationService;

    @TransactionalEventListener
    public void listen(final VoucherStatusUpdateEvent event) {
        String username = event.getUsername();
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

        try {
            notificationService.notifyEvent(username, VOUCHER_STATUS_UPDATE, notification);
            log.info("SSE 알림 전송 완료 (알림 내용: {})", notification);
        } catch (SseUnavailableException e) {
            log.info("SSE 알림 전송 실패 (실패 사유: {})", e.getMessage(), e);
        }
    }
}

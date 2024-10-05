package com.givemecon.common.event.voucher;

import com.givemecon.IntegrationTestEnvironment;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.member.Role;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.common.notification.service.NotificationService;
import com.givemecon.common.notification.util.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static com.givemecon.domain.entity.voucher.VoucherStatus.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

class VoucherStatusUpdateEventListenerTest extends IntegrationTestEnvironment {

    @Autowired
    PlatformTransactionManager txManager;

    @Autowired
    VoucherStatusUpdateEventPublisher eventPublisher;

    @SpyBean
    VoucherStatusUpdateEventListener eventListener;

    @MockBean
    NotificationService notificationService;

    @Test
    @DisplayName("트랜잭션 중에 기프티콘 상태 변경 이벤트 발행 시, 트랜잭션이 커밋된 후 EventListener 작동")
    void voucherStatusUpdateEvent() throws InterruptedException {
        // given
        Member seller = Member.builder()
                .username("seller")
                .role(Role.USER)
                .build();

        VoucherKind voucherKind = VoucherKind.builder()
                .title("Americano T")
                .build();

        Voucher voucher = Voucher.builder()
                .voucherKind(voucherKind)
                .seller(seller)
                .build();

        voucher.updateStatus(FOR_SALE);

        VoucherStatusUpdateEvent event = new VoucherStatusUpdateEvent(voucher);
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);

        // when
        Thread thread = new Thread(() ->
            txTemplate.executeWithoutResult((status) -> eventPublisher.publishEvent(event))
        );

        thread.start();
        thread.join();

        // then
        Mockito.verify(eventListener).listen(event);
        Mockito.verify(notificationService)
                .notifyEvent(eq(seller.getUsername()), eq(EventType.VOUCHER_STATUS_UPDATE), anyString());
    }
}
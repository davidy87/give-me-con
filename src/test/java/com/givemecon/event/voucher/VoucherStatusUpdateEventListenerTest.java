package com.givemecon.event.voucher;

import com.givemecon.IntegrationTestEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static com.givemecon.domain.entity.voucher.VoucherStatus.*;

class VoucherStatusUpdateEventListenerTest extends IntegrationTestEnvironment {

    @Autowired
    PlatformTransactionManager txManager;

    @Autowired
    VoucherStatusUpdateEventPublisher eventPublisher;

    @MockBean
    VoucherStatusUpdateEventListener eventListener;

    @Test
    @DisplayName("트랜잭션 중에 기프티콘 상태 변경 이벤트 발행 시, 트랜잭션이 커밋된 후 EventListener 작동")
    void voucherStatusUpdateEvent() {
        // given
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        VoucherStatusUpdateEvent event = new VoucherStatusUpdateEvent("Americano T", FOR_SALE);

        // when
        Runnable runnable = () -> txTemplate.executeWithoutResult((status) -> eventPublisher.publishEvent(event));
        Thread thread = new Thread(runnable);
        thread.start();

        // then
        Mockito.verify(eventListener).listen(event);
    }
}
package com.givemecon.event.voucher;

import com.givemecon.IntegrationTestEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.givemecon.domain.entity.voucher.VoucherStatus.*;

class VoucherStatusUpdateEventListenerTest extends IntegrationTestEnvironment {

    @Autowired
    VoucherStatusUpdateEventPublisher eventPublisher;

    @MockBean
    VoucherStatusUpdateEventListener eventListener;

    @Test
    @DisplayName("기프티콘 상태 변경 이벤트 발행 시, EventListener 작동")
    void voucherStatusUpdateEvent() {
        // given
        VoucherStatusUpdateEvent event = new VoucherStatusUpdateEvent("Americano T", FOR_SALE);

        // when
        eventPublisher.publishEvent(event);

        // then
        Mockito.verify(eventListener).listen(event);
    }
}
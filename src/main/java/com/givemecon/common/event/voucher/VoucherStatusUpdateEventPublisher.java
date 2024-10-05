package com.givemecon.common.event.voucher;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VoucherStatusUpdateEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishEvent(VoucherStatusUpdateEvent event) {
        eventPublisher.publishEvent(event);
    }
}

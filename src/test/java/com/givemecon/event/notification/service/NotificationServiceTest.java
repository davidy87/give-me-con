package com.givemecon.event.notification.service;

import com.givemecon.IntegrationTestEnvironment;
import com.givemecon.event.notification.service.exception.SseUnavailableException;
import com.givemecon.event.notification.util.EventType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.givemecon.event.notification.util.EventType.*;

class NotificationServiceTest extends IntegrationTestEnvironment {

    @Autowired
    NotificationService notificationService;

    @Test
    @DisplayName("SSE 이벤트 알림 전송 예외 - SSE 연결이 되지 않은 사용자")
    void sseConnectionError() {
        // given
        String username = "tester";
        EventType eventType = VOUCHER_STATUS_UPDATE;
        String data = "Sale confirmed.";

        // when & then
        Assertions.assertThatThrownBy(() -> notificationService.notifyEvent(username, eventType, data))
                .isInstanceOf(SseUnavailableException.class)
                .hasMessage("SSE connection is not established. User = " + username);
    }
}
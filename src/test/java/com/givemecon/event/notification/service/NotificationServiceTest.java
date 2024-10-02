package com.givemecon.event.notification.service;

import com.givemecon.IntegrationTestEnvironment;
import com.givemecon.event.notification.repository.NotificationRepository;
import com.givemecon.event.notification.repository.SseEmitterRepository;
import com.givemecon.event.notification.repository.entity.Notification;
import com.givemecon.event.notification.service.dto.NotificationResponseDto;
import com.givemecon.event.notification.service.exception.SseUnavailableException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static com.givemecon.event.notification.util.EventType.*;
import static org.assertj.core.api.Assertions.*;

class NotificationServiceTest extends IntegrationTestEnvironment {

    @Autowired
    NotificationService notificationService;

    @Autowired
    SseEmitterRepository sseEmitterRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @AfterEach
    void tearDown() {
        sseEmitterRepository.clear();
    }

    @Test
    @DisplayName("사용자 이름으로 Notification 전체 조회 후 DTO 반환")
    void findAllByUsername() {
        // given
        String username = "tester";
        String data = "Sale confirmed.";
        Notification notification = notificationRepository.save(new Notification(username, data));

        // when
        List<NotificationResponseDto> result = notificationService.findAllNotifications(username);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(notification.getId());
        assertThat(result.get(0).getUsername()).isEqualTo(notification.getUsername());
        assertThat(result.get(0).getContent()).isEqualTo(notification.getContent());
    }

    @Test
    @DisplayName("SSE 알림 전송 시, Notification 저장")
    void saveNotificationWhenNotify() {
        // given
        String username = "tester";
        String data = "Sale confirmed.";
        sseEmitterRepository.save(username, new SseEmitter());

        // when
        notificationService.notifyEvent(username, VOUCHER_STATUS_UPDATE, data);

        // then
        List<Notification> notification = notificationRepository.findAllByUsername(username);
        assertThat(notification).hasSize(1);
        assertThat(notification.get(0).getUsername()).isEqualTo(username);
        assertThat(notification.get(0).getContent()).isEqualTo(data);
    }

    @Test
    @DisplayName("SSE 이벤트 알림 전송 예외 - SSE 연결이 되지 않은 사용자")
    void sseConnectionError() {
        // given
        String username = "tester";
        String data = "Sale confirmed.";

        // when & then
        assertThatThrownBy(() -> notificationService.notifyEvent(username, VOUCHER_STATUS_UPDATE, data))
                .isInstanceOf(SseUnavailableException.class)
                .hasMessage("SSE connection is not established. User = " + username);
    }
}
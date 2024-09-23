package com.givemecon.event.notification.service;

import com.givemecon.event.notification.repository.Event;
import com.givemecon.event.notification.repository.EventCache;
import com.givemecon.event.notification.service.exception.SseNotificationException;
import com.givemecon.event.notification.service.exception.SseUnavailableException;
import com.givemecon.event.notification.util.EventIdUtils;
import com.givemecon.event.notification.repository.SseEmitterRepository;
import com.givemecon.event.notification.util.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;

import static com.givemecon.event.notification.service.exception.errorcode.SseErrorCode.*;
import static com.givemecon.event.notification.util.EventType.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private static final long TIMEOUT = Duration.ofHours(1).toMillis();

    private final SseEmitterRepository sseEmitterRepository;

    private final EventCache eventCache;

    public SseEmitter subscribe(String username, String lastEventId) {
        SseEmitter sseEmitter = sseEmitterRepository.save(username, new SseEmitter(TIMEOUT));
        setEmitterAction(sseEmitter, username);

        if (StringUtils.hasText(lastEventId)) {
            notifyOmittedEvents(sseEmitter, username);
        } else {
            notifyDummy(sseEmitter, username);
        }

        return sseEmitter;
    }

    /**
     * 이벤트가 발생하는 곳에서 알림을 전송하기 위해 호출하는 메서드
     */
    public void notifyEvent(String username, EventType eventType, Object data) {
        SseEmitter sseEmitter = sseEmitterRepository.findByUsername(username)
                .orElseThrow(() -> {
                    String errorMessage = "SSE connection is not established. User = " + username;
                    return new SseUnavailableException(CONNECTION_NOT_ESTABLISHED, errorMessage);
                });

        String eventId = EventIdUtils.createEventId(username);
        String eventName = eventType.getEventName();

        eventCache.save(eventId, new Event(eventName, data));
        notify(sseEmitter, eventId, eventName, data);
        sseEmitter.complete();
    }

    /**
     * 첫 SSE 연결 후, 더미 데이터를 보내기 위해 호출하는 메서드
     */
    private void notifyDummy(SseEmitter sseEmitter, String username) {
        String eventId = "";
        String eventName = SSE_SUBSCRIPTION.getEventName();
        String data = "SSE connected. Connected user = " + username;

        notify(sseEmitter, eventId, eventName, data);
    }

    /**
     * 알림 전송이 누락된 이벤트들을 조회한 후, 재전송하기 위해 호출하는 메서드
     */
    private void notifyOmittedEvents(SseEmitter sseEmitter, String username) {
        eventCache.findAllOmittedEvents(username)
                .forEach((eventId, event) -> {
                    notify(sseEmitter, eventId, event.getEventName(), event.getData());
                    eventCache.deleteByEventId(eventId);
                });
    }

    private void notify(SseEmitter sseEmitter, String eventId, String eventName, Object data) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(eventId)
                    .name(eventName)
                    .data(data));
        } catch (IOException e) {
            log.info("Exception occurred while sending notification.");
            String username = EventIdUtils.parseUsername(eventId);
            sseEmitterRepository.deleteByUsername(username);
            throw new SseNotificationException(NOTIFICATION_ERROR);
        }
    }

    private void setEmitterAction(SseEmitter sseEmitter, String username) {
        sseEmitter.onCompletion(() -> {
            log.info("SSE completed: subscriber = {}", username);
            sseEmitterRepository.deleteByUsername(username);
        });

        sseEmitter.onTimeout(() -> {
            log.info("SSE timeout: subscriber = {}", username);
            sseEmitter.complete();
        });

        sseEmitter.onError((e) -> {
            log.info("SSE error: subscriber = {}", username, e);
            sseEmitter.complete();
        });
    }
}

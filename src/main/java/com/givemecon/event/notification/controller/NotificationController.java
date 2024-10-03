package com.givemecon.event.notification.controller;

import com.givemecon.event.notification.service.NotificationService;
import com.givemecon.event.notification.service.exception.SseUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.givemecon.event.notification.service.dto.NotificationDto.*;
import static com.givemecon.event.notification.service.exception.errorcode.SseErrorCode.SUBSCRIBER_NOT_PRESENTED;
import static org.springframework.data.domain.Sort.Direction.*;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RequiredArgsConstructor
@RequestMapping("/api/sse")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(Authentication authentication,
                                @RequestHeader(value = "Last-Event-Id", required = false) String lastEventId) {

        if (authentication == null) {
            throw new SseUnavailableException(SUBSCRIBER_NOT_PRESENTED);
        }

        return notificationService.subscribe(authentication.getName(), lastEventId);
    }

    @GetMapping("/notifications")
    public PagedNotificationResponse findAllNotification(Authentication authentication,
                                                         @PageableDefault(
                                                                 size = 5,
                                                                 sort = "id",
                                                                 direction = DESC) Pageable pageable) {

        return notificationService.findPagedNotifications(authentication.getName(), pageable);
    }
}

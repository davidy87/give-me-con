package com.givemecon.common.notification.service.dto;

import com.givemecon.common.notification.repository.entity.Notification;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationDto {

    @Getter
    public static class NotificationResponse {

        private final Long id;

        private final String username;

        private final String content;

        public NotificationResponse(Notification notification) {
            this.id = notification.getId();
            this.username = notification.getUsername();
            this.content = notification.getContent();
        }
    }

    @Getter
    public static class PagedNotificationResponse {

        private final int number;

        private final int totalPages;

        private final int size;

        private final List<NotificationResponse> notifications;

        public PagedNotificationResponse(Page<NotificationResponse> page) {
            this.number = page.getNumber();
            this.totalPages = page.getTotalPages();
            this.size = page.getSize();
            this.notifications = page.getContent();
        }
    }
}

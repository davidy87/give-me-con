package com.givemecon.event.notification.service.dto;

import com.givemecon.event.notification.repository.entity.Notification;
import lombok.Getter;

@Getter
public class NotificationResponseDto {

    private final Long id;

    private final String username;

    private final String content;

    public NotificationResponseDto(Notification notification) {
        this.id = notification.getId();
        this.username = notification.getUsername();
        this.content = notification.getContent();
    }
}

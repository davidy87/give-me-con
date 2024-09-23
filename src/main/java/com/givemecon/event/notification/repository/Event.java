package com.givemecon.event.notification.repository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Event {

    private final String eventName;

    private final Object data;
}

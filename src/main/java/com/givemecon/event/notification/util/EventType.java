package com.givemecon.event.notification.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {

    SSE_SUBSCRIPTION("connection"),
    SALE_CONFIRMATION("sale-confirmation");

    private final String eventName;
}

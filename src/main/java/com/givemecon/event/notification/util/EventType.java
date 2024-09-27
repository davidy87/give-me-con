package com.givemecon.event.notification.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {

    SSE_SUBSCRIPTION("connection"),
    VOUCHER_STATUS_UPDATE("voucher-status-update");

    private final String eventName;
}

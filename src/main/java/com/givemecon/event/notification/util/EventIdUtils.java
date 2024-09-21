package com.givemecon.event.notification.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventIdUtils {

    private static final String DELIMITER = "-";

    public static String createEventId(String username) {
        return username + DELIMITER + System.currentTimeMillis();
    }

    public static String parseUsername(String eventId) {
        return eventId.split(DELIMITER)[0];
    }
}

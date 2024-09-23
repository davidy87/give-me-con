package com.givemecon.event.notification.service.exception;

import com.givemecon.common.error.ErrorCode;
import com.givemecon.common.exception.GivemeconException;

public class SseNotificationException extends GivemeconException {

    public SseNotificationException(ErrorCode errorCode) {
        super(errorCode);
    }
}

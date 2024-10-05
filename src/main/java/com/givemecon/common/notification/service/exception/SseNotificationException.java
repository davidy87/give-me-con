package com.givemecon.common.notification.service.exception;

import com.givemecon.common.error.ErrorCode;
import com.givemecon.common.exception.GivemeconException;

public class SseNotificationException extends GivemeconException {

    public SseNotificationException(ErrorCode errorCode) {
        super(errorCode);
    }
}

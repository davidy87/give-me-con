package com.givemecon.event.notification.service.exception;

import com.givemecon.common.error.ErrorCode;
import com.givemecon.common.exception.GivemeconException;

public class SseUnavailableException extends GivemeconException {

    public SseUnavailableException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

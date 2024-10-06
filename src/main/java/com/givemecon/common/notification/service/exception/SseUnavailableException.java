package com.givemecon.common.notification.service.exception;

import com.givemecon.common.error.ErrorCode;
import com.givemecon.common.exception.GivemeconException;

public class SseUnavailableException extends GivemeconException {

    public SseUnavailableException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SseUnavailableException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

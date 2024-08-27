package com.givemecon.application.exception;

import com.givemecon.common.error.ErrorCode;
import com.givemecon.common.exception.GivemeconException;

public class InvalidOrderException extends GivemeconException {

    public InvalidOrderException(ErrorCode errorCode) {
        super(errorCode);
    }
}

package com.givemecon.application.exception.order;

import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.exception.GivemeconException;

public class InvalidOrderException extends GivemeconException {

    public InvalidOrderException(ErrorCode errorCode) {
        super(errorCode);
    }
}

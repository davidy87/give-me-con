package com.givemecon.domain.order.exception;

import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.exception.GivemeconException;

public class InvalidOrderException extends GivemeconException {

    public InvalidOrderException(ErrorCode errorCode) {
        super(errorCode);
    }
}

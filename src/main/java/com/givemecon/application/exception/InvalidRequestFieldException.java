package com.givemecon.application.exception;

import com.givemecon.common.error.ErrorCode;
import com.givemecon.common.exception.GivemeconException;

public class InvalidRequestFieldException extends GivemeconException {

    public InvalidRequestFieldException(ErrorCode errorCode) {
        super(errorCode);
    }
}

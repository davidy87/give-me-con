package com.givemecon.util.exception.concrete;

import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.exception.GivemeconException;

public class InvalidTokenException extends GivemeconException {

    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }
}

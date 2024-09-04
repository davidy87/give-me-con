package com.givemecon.common.auth.jwt.exception;

import com.givemecon.common.error.ErrorCode;
import com.givemecon.common.exception.GivemeconException;

public class InvalidTokenException extends GivemeconException {

    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }
}

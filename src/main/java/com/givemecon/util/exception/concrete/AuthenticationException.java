package com.givemecon.util.exception.concrete;

import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.exception.GivemeconException;

public class AuthenticationException extends GivemeconException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }
}

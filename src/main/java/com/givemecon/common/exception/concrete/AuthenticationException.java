package com.givemecon.common.exception.concrete;

import com.givemecon.common.error.ErrorCode;
import com.givemecon.common.exception.GivemeconException;

public class AuthenticationException extends GivemeconException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
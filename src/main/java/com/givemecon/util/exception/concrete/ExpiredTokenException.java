package com.givemecon.util.exception.concrete;

import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.exception.GivemeconException;

public class ExpiredTokenException extends GivemeconException {

    public ExpiredTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}

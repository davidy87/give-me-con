package com.givemecon.util.exception.concrete;

import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.exception.GivemeconException;

public class EntityNotFoundException extends GivemeconException {

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

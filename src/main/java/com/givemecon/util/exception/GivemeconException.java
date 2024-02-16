package com.givemecon.util.exception;

import com.givemecon.util.error.ErrorCode;
import lombok.Getter;

@Getter
public abstract class GivemeconException extends RuntimeException {

    private final ErrorCode errorCode;

    protected GivemeconException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}

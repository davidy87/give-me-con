package com.givemecon.util.exception;

import com.givemecon.util.error.ErrorCode;
import lombok.Getter;

@Getter
public class GivemeconException extends RuntimeException {

    private final ErrorCode errorCode;

    protected GivemeconException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    protected GivemeconException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }
}

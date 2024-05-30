package com.givemecon.util.exception;

import com.givemecon.util.error.ErrorCode;
import lombok.Getter;

@Getter
public class GivemeconException extends RuntimeException {

    private final ErrorCode errorCode;

    protected GivemeconException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    protected GivemeconException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected GivemeconException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    protected GivemeconException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}

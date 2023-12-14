package com.givemecon.util.exception;

import com.givemecon.util.error.ErrorCode;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    private final int status;

    private final String code;

    private final String message;

    public EntityNotFoundException(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}

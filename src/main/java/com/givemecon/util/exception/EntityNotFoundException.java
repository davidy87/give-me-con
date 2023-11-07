package com.givemecon.util.exception;

import com.givemecon.util.error.ErrorCode;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    private String status;

    private String code;

    private String message;

    public EntityNotFoundException(ErrorCode errorCode) {
        this.status = errorCode.name();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}

package com.givemecon.util.error;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final int status;

    private final String code;

    private final String message;

    @Builder
    public ErrorResponse(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}

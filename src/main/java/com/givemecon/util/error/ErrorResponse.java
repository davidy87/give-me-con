package com.givemecon.util.error;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private String code;

    private int status;

    private String message;
}

package com.givemecon.util.error;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private String status;

    private String code;

    private String message;
}

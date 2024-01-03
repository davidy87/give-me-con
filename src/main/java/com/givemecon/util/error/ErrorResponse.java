package com.givemecon.util.error;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ErrorResponse {

    private int status;

    private String code;

    private String message;
}

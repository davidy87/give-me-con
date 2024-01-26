package com.givemecon.util.error;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationErrorResponse extends ErrorResponse {

    private final List<FieldErrorResponse> fieldErrors = new ArrayList<>();

    public ValidationErrorResponse(ErrorCode errorCode) {
        super(errorCode);
    }

    public void addFieldError(FieldErrorResponse fieldErrorResponse) {
        fieldErrors.add(fieldErrorResponse);
    }
}

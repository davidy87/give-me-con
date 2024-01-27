package com.givemecon.util.error.response;

import com.givemecon.util.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationErrorResponse extends ErrorResponse {

    private final List<FieldErrorResponse> fieldErrors = new ArrayList<>();

    public ValidationErrorResponse(ErrorCode errorCode) {
        super(errorCode);
    }

    public void addFieldMessage(String field, String message) {
        fieldErrors.add(new FieldErrorResponse(field, message));
    }

    @Getter
    @RequiredArgsConstructor
    private static class FieldErrorResponse {

        private final String field;

        private final String message;
    }
}

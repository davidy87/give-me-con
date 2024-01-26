package com.givemecon.util.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FieldErrorResponse {

    private final String field;

    private final String message;
}

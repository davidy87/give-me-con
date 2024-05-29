package com.givemecon.util.error.response;

import com.givemecon.util.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class MissingParameterErrorResponse extends ErrorResponse {

    private final ParameterDetails parameterDetails;

    public MissingParameterErrorResponse(ErrorCode errorCode, String parameterName, String parameterType) {
        super(errorCode);
        this.parameterDetails = new ParameterDetails(parameterName, parameterType);
    }

    @Getter
    @RequiredArgsConstructor
    private static class ParameterDetails {

        private final String parameterName;

        private final String parameterType;
    }
}

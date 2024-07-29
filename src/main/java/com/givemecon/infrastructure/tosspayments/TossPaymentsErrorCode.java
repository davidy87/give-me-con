package com.givemecon.infrastructure.tosspayments;

import com.givemecon.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class TossPaymentsErrorCode implements ErrorCode {

    @Setter
    private int status;

    private String code;

    private String message;

    @Override
    public String toString() {
        return "{" +
                "status=" + status +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

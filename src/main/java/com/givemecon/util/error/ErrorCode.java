package com.givemecon.util.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_FOUND("001", "Entity Not Found (존재하지 않는 Entity입니다.)");

    private String code;
    private String message;
}

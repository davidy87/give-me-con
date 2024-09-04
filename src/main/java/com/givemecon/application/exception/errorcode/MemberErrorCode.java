package com.givemecon.application.exception.errorcode;

import com.givemecon.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST.value(), "INVALID_MEMBER_ID", "Member id가 올바르지 않습니다."),
    INVALID_USERNAME(HttpStatus.BAD_REQUEST.value(), "INVALID_USERNAME", "Username이 올바르지 않습니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST.value(), "PASSWORD_NOT_MATCH", "비밀번호가 일치하지 않습니다."),
    ROLE_NOT_ADMIN(HttpStatus.BAD_REQUEST.value(), "ROLE_NOT_ADMIN", "어드민 계정이 아닙니다.");

    private final int status;

    private final String code;

    private final String message;
}

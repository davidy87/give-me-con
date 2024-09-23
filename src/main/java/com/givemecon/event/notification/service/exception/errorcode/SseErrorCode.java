package com.givemecon.event.notification.service.exception.errorcode;

import com.givemecon.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SseErrorCode implements ErrorCode {

    CONNECTION_NOT_ESTABLISHED(HttpStatus.BAD_REQUEST.value(), "CONNECTION_NOT_ESTABLISHED", "SSE 연결이 되어있지 않습니다."),
    NOTIFICATION_ERROR(HttpStatus.BAD_REQUEST.value(), "NOTIFICATION_ERROR", "알림 전송 중 오류가 발생했습니다.");

    private final int status;

    private final String code;

    private final String message;
}

package com.givemecon.infrastructure.s3.exception;

import com.givemecon.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum S3ErrorCode implements ErrorCode {

    IMAGE_PROCESS_FAILED(HttpStatus.BAD_REQUEST.value(), "IMAGE_PROCESS_FAILED", "이미지 파일 업로드에 실패하였습니다. 나중에 다시 시도해주세요.");

    private final int status;

    private final String code;

    private final String message;
}

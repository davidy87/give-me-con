package com.givemecon.common.exception.concrete;

import com.givemecon.common.error.ErrorCode;
import com.givemecon.common.exception.GivemeconException;
import lombok.Getter;

@Getter
public class FileNotProcessedException extends GivemeconException {

    public FileNotProcessedException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public FileNotProcessedException(ErrorCode errorCode, String rejectedFilename, Throwable cause) {
        super(errorCode, errorCode.getMessage() + " Filename: " + rejectedFilename, cause);
    }
}
package com.givemecon.util.exception.concrete;

import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.exception.GivemeconException;
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
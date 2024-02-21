package com.givemecon.util.exception.concrete;

import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.exception.GivemeconException;
import lombok.Getter;

@Getter
public class FileNotProcessedException extends GivemeconException {

    private final String rejectedFilename;

    public FileNotProcessedException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
        this.rejectedFilename = null;
    }

    public FileNotProcessedException(ErrorCode errorCode, String rejectedFilename, Throwable cause) {
        super(errorCode, cause);
        this.rejectedFilename = rejectedFilename;
    }
}
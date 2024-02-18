package com.givemecon.util.exception.concrete;

import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.exception.GivemeconException;

public class FileProcessException extends GivemeconException {

    private final String rejectedFilename;

    public FileProcessException(ErrorCode errorCode, String rejectedFilename) {
        super(errorCode);
        this.rejectedFilename = rejectedFilename;
    }
}
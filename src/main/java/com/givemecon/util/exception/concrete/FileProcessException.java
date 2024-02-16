package com.givemecon.util.exception.concrete;

import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.exception.GivemeconException;

public class FileProcessException extends GivemeconException {

    public FileProcessException(ErrorCode errorCode) {
        super(errorCode);
    }
}

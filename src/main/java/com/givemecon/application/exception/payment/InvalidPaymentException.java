package com.givemecon.application.exception.payment;

import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.exception.GivemeconException;

public class InvalidPaymentException extends GivemeconException {

    public InvalidPaymentException(ErrorCode errorCode) {
        super(errorCode);
    }
}

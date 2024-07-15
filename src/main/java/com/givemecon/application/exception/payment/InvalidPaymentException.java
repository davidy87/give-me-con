package com.givemecon.application.exception.payment;

import com.givemecon.common.error.ErrorCode;
import com.givemecon.common.exception.GivemeconException;

public class InvalidPaymentException extends GivemeconException {

    public InvalidPaymentException(ErrorCode errorCode) {
        super(errorCode);
    }
}

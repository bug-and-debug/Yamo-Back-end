package com.locassa.yamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class AppleReceiptException extends RuntimeException {

    public AppleReceiptException(String info) {
        super(null == info ? "Could not validate Apple receipt." : info);
    }

}

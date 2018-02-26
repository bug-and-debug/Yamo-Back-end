package com.locassa.yamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserSubscriptionException extends RuntimeException {

    public UserSubscriptionException() {
        super("The dates extracted for the Apple receipt are not valid.");
    }

    public UserSubscriptionException(boolean flag) {
        super("The subscription end date is expired.");
    }

}

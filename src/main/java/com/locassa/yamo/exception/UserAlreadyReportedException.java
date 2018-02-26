package com.locassa.yamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserAlreadyReportedException extends RuntimeException {

    public UserAlreadyReportedException(String authEmail, String existingEmail) {
        super(String.format("User '%s' already reported user '%s'.", authEmail, existingEmail));
    }

}

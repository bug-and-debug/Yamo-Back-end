package com.locassa.yamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class WrongCredentialsException extends RuntimeException {

    public WrongCredentialsException() {
        super("Could not sign user in. Wrong credentials.");
    }

}

package com.locassa.yamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class AppleIdAlreadyUsedException extends RuntimeException {

    public AppleIdAlreadyUsedException() {
        super("This Apple ID account was already used to promote a Vesta user.");
    }

}
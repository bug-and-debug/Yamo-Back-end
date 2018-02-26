package com.locassa.yamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class ConnectWithFacebookException extends RuntimeException {

    public ConnectWithFacebookException() {
        super("You cannot connect with Facebook because the retrieved email from FB already exists in the DB.");
    }

}

package com.locassa.yamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Could not find user.");
    }

    public UserNotFoundException(String email) {
        super(String.format("Could not find user with email '%s'.", email));
    }

    public UserNotFoundException(Long userId) {
        super(String.format("Could not find user with Id '%s'.", userId));
    }

}

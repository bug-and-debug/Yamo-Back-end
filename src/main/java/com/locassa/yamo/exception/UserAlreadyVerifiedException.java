package com.locassa.yamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserAlreadyVerifiedException extends RuntimeException {

    public UserAlreadyVerifiedException(String email) {
        super(String.format("User with email '%s' already verified the account.", email));
    }

}

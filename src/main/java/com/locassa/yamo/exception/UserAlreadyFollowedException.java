package com.locassa.yamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserAlreadyFollowedException extends RuntimeException {

    public UserAlreadyFollowedException(String authEmail, String existingEmail) {
        super(String.format("User '%s' already followed user '%s'.", authEmail, existingEmail));
    }

    public UserAlreadyFollowedException(String authEmail, String existingEmail, String unFollow) {
        super(String.format("User '%s' was not following user '%s'.", authEmail, existingEmail));
    }

}

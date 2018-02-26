package com.locassa.yamo.exception;

import com.locassa.yamo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class OwnershipException extends RuntimeException {

    public OwnershipException(Class<? extends Serializable> obj) {
        super(String.format("An operation for object of type '%s' is not allowed by user.", obj.getSimpleName()));
    }

    public OwnershipException(Class<? extends Serializable> obj, User user) {
        super(String.format("An operation for object of type '%s' is not allowed by user '%s'.", obj.getSimpleName(), user.getEmail()));
    }

    public OwnershipException(Class<? extends Serializable> obj, String value) {
        super(String.format("An operation for object of type '%s' and Id or value '%s' is not allowed by user.", obj.getSimpleName(), value));
    }

    public OwnershipException(Class<? extends Serializable> obj, String value, User user) {
        super(String.format("An operation for object of type '%s' and Id or value '%s' is not allowed by user '%s'.", obj.getSimpleName(), value, user.getEmail()));
    }

}

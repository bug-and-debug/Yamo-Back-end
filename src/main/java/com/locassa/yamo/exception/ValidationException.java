package com.locassa.yamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.PRECONDITION_REQUIRED)
public class ValidationException extends RuntimeException {

    public ValidationException(String field, String wrongValue) {
        super(String.format("Value '%s' is wrong for field '%s'.", wrongValue, field));
    }

}

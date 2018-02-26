package com.locassa.yamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArtWorkNotFoundException extends RuntimeException {

    public ArtWorkNotFoundException(Long id) {
        super(String.format("Could not find art work with Id '%s'.", id));
    }

}

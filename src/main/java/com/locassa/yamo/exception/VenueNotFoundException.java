package com.locassa.yamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class VenueNotFoundException extends RuntimeException {

    public VenueNotFoundException() {
        super("Could not find venue.");
    }

    public VenueNotFoundException(Long venueId) {
        super(String.format("Could not find venue with Id '%s'.", venueId));
    }


}

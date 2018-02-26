package com.locassa.yamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserMarkedVenueAsInterestingException extends RuntimeException {

    public UserMarkedVenueAsInterestingException(String authEmail, String existingVenueName) {
        super(String.format("User '%s' already marked venue '%s' as interesting.", authEmail, existingVenueName));
    }

    public UserMarkedVenueAsInterestingException(String authEmail, String existingVenueName, String notInteresting) {
        super(String.format("Venue '%s' was not an interesting venue for user '%s'.", existingVenueName, authEmail));
    }

}

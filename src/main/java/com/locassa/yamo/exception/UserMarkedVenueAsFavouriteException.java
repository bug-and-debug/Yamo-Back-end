package com.locassa.yamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserMarkedVenueAsFavouriteException extends RuntimeException {

    public UserMarkedVenueAsFavouriteException(String authEmail, String existingVenueName) {
        super(String.format("User '%s' already marked venue '%s' as favourite.", authEmail, existingVenueName));
    }

    public UserMarkedVenueAsFavouriteException(String authEmail, String existingVenueName, String unFavourite) {
        super(String.format("Venue '%s' was not a favourite venue for user '%s'.", existingVenueName, authEmail));
    }

}

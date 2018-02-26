package com.locassa.yamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RouteNotFoundException extends RuntimeException {

    public RouteNotFoundException() {
        super("Could not find route.");
    }

    public RouteNotFoundException(Long routeId) {
        super(String.format("Could not find route with Id '%s'.", routeId));
    }

}

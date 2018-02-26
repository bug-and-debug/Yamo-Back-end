package com.locassa.yamo.model.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum ContentType implements Serializable {

    UNSPECIFIED(0),
    VENUE(1), // Venue can be all available types in VenueType.
    MOVEMENT(2),
    MEDIUM(3);

    private static final Map<Integer, ContentType> intToTypeMap = new HashMap<>();

    static {
        for (ContentType type : ContentType.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    private final int value;

    private ContentType(int value) {
        this.value = value;
    }

    public static ContentType fromInt(int value) {
        return intToTypeMap.get(value);
    }

    public int getValue() {
        return this.value;
    }

}

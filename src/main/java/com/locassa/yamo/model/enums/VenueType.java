package com.locassa.yamo.model.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum VenueType implements Serializable {

    UNSPECIFIED(0),
    EXHIBITION(1),
    GALLERY(2);

    private static final Map<Integer, VenueType> intToTypeMap = new HashMap<>();

    static {
        for (VenueType type : VenueType.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    private final int value;

    private VenueType(int value) {
        this.value = value;
    }

    public static VenueType fromInt(int value) {
        return intToTypeMap.get(value);
    }

    public int getValue() {
        return this.value;
    }

}

package com.locassa.yamo.model.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum NotificationType implements Serializable {

    UNSPECIFIED(0),
    FACEBOOK_FRIEND_JOINED(1), // OK
    EXHIBITION_SUGGESTION(2), // OK
    GET_TO_KNOW_ME_REMINDER(3), // OK
    CURRENT_LOCATION_SUGGESTION(4), // ?
    EXHIBITION_CLOSING_SOON(5), // OK
    NEW_EXHIBITION_AT_FAVOURITE_GALLERY(6) // CMS OK
    ;

    private static final Map<Integer, NotificationType> intToTypeMap = new HashMap<>();

    static {
        for (NotificationType type : NotificationType.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    private final int value;

    private NotificationType(int value) {
        this.value = value;
    }

    public static NotificationType fromInt(int value) {
        return intToTypeMap.get(value);
    }

    public int getValue() {
        return this.value;
    }

}

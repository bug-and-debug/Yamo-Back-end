package com.locassa.yamo.model.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum NotificationPlatform implements Serializable {

    APNS(0),
    APNS_SANDBOX(1),
    GCM(2);

    private static final Map<Integer, NotificationPlatform> intToTypeMap = new HashMap<Integer, NotificationPlatform>();

    static {
        for (NotificationPlatform type : NotificationPlatform.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    private final int value;

    private NotificationPlatform(int value) {
        this.value = value;
    }

    public static NotificationPlatform fromInt(int value) {
        return intToTypeMap.get(value);
    }

    public int getValue() {
        return this.value;
    }

}

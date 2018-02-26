package com.locassa.yamo.model.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum PlatformType implements Serializable {

    APNS(0),
    APNS_SANDBOX(1),
    GCM(2);

    private static final Map<Integer, PlatformType> intToTypeMap = new HashMap<>();

    static {
        for (PlatformType type : PlatformType.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    private final int value;

    private PlatformType(int value) {
        this.value = value;
    }

    public static PlatformType fromInt(int value) {
        return intToTypeMap.get(value);
    }

    public int getValue() {
        return this.value;
    }

}

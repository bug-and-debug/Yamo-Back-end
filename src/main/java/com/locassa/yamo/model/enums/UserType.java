package com.locassa.yamo.model.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum UserType implements Serializable {

    UNSPECIFIED(0),
    GUEST(1),
    USER(2),
    ADMIN(3),
    GLOBAL(4);

    private static final Map<Integer, UserType> intToTypeMap = new HashMap<>();

    static {
        for (UserType type : UserType.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    private final int value;

    private UserType(int value) {
        this.value = value;
    }

    public static UserType fromInt(int value) {
        return intToTypeMap.get(value);
    }

    public int getValue() {
        return this.value;
    }

}

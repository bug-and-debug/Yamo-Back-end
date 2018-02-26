package com.locassa.yamo.model.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum SubscriptionType implements Serializable {

    UNSPECIFIED(0),
    APPLE(1),
    GOOGLE(2);

    private static final Map<Integer, SubscriptionType> intToTypeMap = new HashMap<Integer, SubscriptionType>();

    static {
        for (SubscriptionType type : SubscriptionType.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    private final int value;

    private SubscriptionType(int value) {
        this.value = value;
    }

    public static SubscriptionType fromInt(int value) {
        return intToTypeMap.get(value);
    }

    public int getValue() {
        return this.value;
    }

}
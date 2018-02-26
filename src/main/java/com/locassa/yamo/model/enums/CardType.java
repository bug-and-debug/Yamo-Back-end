package com.locassa.yamo.model.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum CardType implements Serializable {

    UNSPECIFIED(0),
    SINGLE(1),
    MULTIPLE(2);

    private static final Map<Integer, CardType> intToTypeMap = new HashMap<>();

    static {
        for (CardType type : CardType.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    private final int value;

    private CardType(int value) {
        this.value = value;
    }

    public static CardType fromInt(int value) {
        return intToTypeMap.get(value);
    }

    public int getValue() {
        return this.value;
    }

}

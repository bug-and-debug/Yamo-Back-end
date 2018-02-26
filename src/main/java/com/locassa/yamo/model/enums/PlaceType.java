package com.locassa.yamo.model.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum PlaceType implements Serializable {

    UNSPECIFIED(0),
    HOME(1),
    WORK(2);

    private static final Map<Integer, PlaceType> intToTypeMap = new HashMap<>();

    static {
        for (PlaceType type : PlaceType.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    private final int value;

    private PlaceType(int value) {
        this.value = value;
    }

    public static PlaceType fromInt(int value) {
        return null == intToTypeMap.get(value) ? UNSPECIFIED : intToTypeMap.get(value);
    }

    public int getValue() {
        return this.value;
    }

}

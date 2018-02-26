package com.locassa.yamo.model.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum AnswerType implements Serializable {

    UNANSWERED(0),
    LIKED(1),
    DISMISSED(2),
    SKIPPED(3);

    private static final Map<Integer, AnswerType> intToTypeMap = new HashMap<>();

    static {
        for (AnswerType type : AnswerType.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    private final int value;

    private AnswerType(int value) {
        this.value = value;
    }

    public static AnswerType fromInt(int value) {
        return intToTypeMap.get(value);
    }

    public int getValue() {
        return this.value;
    }

}

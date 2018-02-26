package com.locassa.yamo.model.dto;

import java.io.Serializable;

public class BooleanDTO implements Serializable {

    private boolean value;

    public BooleanDTO() {
    }

    public BooleanDTO(boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}

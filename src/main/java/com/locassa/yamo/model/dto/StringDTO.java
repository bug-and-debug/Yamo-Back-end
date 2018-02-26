package com.locassa.yamo.model.dto;

import java.io.Serializable;

public class StringDTO implements Serializable {

    private String value;

    public StringDTO() {}

    public StringDTO(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}

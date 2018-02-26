package com.locassa.yamo.model.dto;

import java.io.Serializable;

public class DeviceDTO implements Serializable {

    private int platform;

    private String token;

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}

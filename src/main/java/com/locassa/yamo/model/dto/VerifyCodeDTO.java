package com.locassa.yamo.model.dto;

import java.io.Serializable;

public class VerifyCodeDTO implements Serializable {

    private String email;

    private String secretCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }
}

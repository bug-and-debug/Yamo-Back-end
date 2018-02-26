package com.locassa.yamo.model.view;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserLabelNormalisedValueKey implements Serializable {

    private Long userUuid;

    private String userEmail;

    private Long tagUuid;

    private String tagName;

    private double nValue;

    public Long getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(Long userUuid) {
        this.userUuid = userUuid;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getTagUuid() {
        return tagUuid;
    }

    public void setTagUuid(Long tagUuid) {
        this.tagUuid = tagUuid;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public double getnValue() {
        return nValue;
    }

    public void setnValue(double nValue) {
        this.nValue = nValue;
    }
}

package com.locassa.yamo.model.view;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "v_user_label_n_values")
public class UserLabelNormalisedValue implements Serializable {

    @EmbeddedId
    @JsonUnwrapped
    private UserLabelNormalisedValueKey key;

    private String tagColour;

    public UserLabelNormalisedValueKey getKey() {
        return key;
    }

    public void setKey(UserLabelNormalisedValueKey key) {
        this.key = key;
    }

    public String getTagColour() {
        return tagColour;
    }

    public void setTagColour(String tagColour) {
        this.tagColour = tagColour;
    }
}

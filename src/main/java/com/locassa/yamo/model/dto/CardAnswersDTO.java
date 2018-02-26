package com.locassa.yamo.model.dto;

import java.io.Serializable;
import java.util.List;

public class CardAnswersDTO implements Serializable {

    private List<Long> positive;

    private List<Long> negative;

    private List<Long> skipped;

    public List<Long> getPositive() {
        return positive;
    }

    public void setPositive(List<Long> positive) {
        this.positive = positive;
    }

    public List<Long> getNegative() {
        return negative;
    }

    public void setNegative(List<Long> negative) {
        this.negative = negative;
    }

    public List<Long> getSkipped() {
        return skipped;
    }

    public void setSkipped(List<Long> skipped) {
        this.skipped = skipped;
    }
}

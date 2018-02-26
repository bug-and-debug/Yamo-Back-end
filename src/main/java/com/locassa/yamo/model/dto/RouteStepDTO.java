package com.locassa.yamo.model.dto;

import java.io.Serializable;

public class RouteStepDTO implements Serializable {

    private int sequenceOrder;

    private Long venueId;

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }
}

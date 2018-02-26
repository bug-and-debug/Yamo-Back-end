package com.locassa.yamo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class RouteStep extends AuditEntity implements Serializable, Comparable<RouteStep> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uuid;

    private int sequenceOrder;

    @ManyToOne
    private Venue venue;

    @ManyToOne
    @JsonIgnore
    private Route parent;

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    @Override
    public int compareTo(RouteStep o) {
        if (null == o) {
            return 1;
        } else {
            return Integer.valueOf(this.sequenceOrder).compareTo(o.getSequenceOrder());
        }
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof RouteStep && ((RouteStep) obj).getSequenceOrder() == this.sequenceOrder;
    }

    public Route getParent() {
        return parent;
    }

    public void setParent(Route parent) {
        this.parent = parent;
    }

}

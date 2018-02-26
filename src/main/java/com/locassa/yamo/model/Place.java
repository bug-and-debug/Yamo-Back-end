package com.locassa.yamo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.locassa.yamo.model.enums.PlaceType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Place extends AuditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uuid;

    private PlaceType placeType;

    @Transient
    private int placeTypeValue;

    @ManyToOne
    @JsonIgnore
    private User user;

    @Transient
    private Long userId;

    private double lat;

    private double lon;

    private String location;

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }

    public int getPlaceTypeValue() {
        if (PlaceType.UNSPECIFIED.getValue() != placeTypeValue) {
            return placeTypeValue;
        } else {
            return null == placeType ? PlaceType.UNSPECIFIED.getValue() : placeType.getValue();
        }
    }

    public void setPlaceTypeValue(int placeTypeValue) {
        this.placeTypeValue = placeTypeValue;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return null == user ? -1 : user.getUuid();
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

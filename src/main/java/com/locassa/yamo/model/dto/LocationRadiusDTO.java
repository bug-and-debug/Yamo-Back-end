package com.locassa.yamo.model.dto;

import java.io.Serializable;

public class LocationRadiusDTO implements Serializable {

    private double lat;

    private double lon;

    private double miles;

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

    public double getMiles() {
        return miles;
    }

    public void setMiles(double miles) {
        this.miles = miles;
    }
}

package com.locassa.yamo.model.dto;

import com.locassa.yamo.util.YamoUtils;

import java.io.Serializable;
import java.util.List;

public class FilterDTO implements Serializable {

    private double lat;

    private double lon;

    private boolean mostPopular;

    private List<Long> tagIds;

    private double miles;

    private int priceFilter;

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

    public boolean isMostPopular() {
        return mostPopular;
    }

    public void setMostPopular(boolean mostPopular) {
        this.mostPopular = mostPopular;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public double getMiles() {
        return 0 == miles ? YamoUtils.DEFAULT_MILES_FOR_FILTER : miles;
    }

    public void setMiles(double miles) {
        this.miles = miles;
    }

    public int getPriceFilter() {
        return priceFilter;
    }

    public void setPriceFilter(int priceFilter) {
        this.priceFilter = priceFilter;
    }
}

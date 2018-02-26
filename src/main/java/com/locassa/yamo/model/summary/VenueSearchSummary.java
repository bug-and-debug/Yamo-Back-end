package com.locassa.yamo.model.summary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.locassa.yamo.model.Tag;
import com.locassa.yamo.model.enums.VenueType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "venue")
public class VenueSearchSummary implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uuid;

    private String name;

    @Column(columnDefinition = "VARCHAR(4000)")
    private String description;

    private VenueType venueType;

    @Transient
    private int venueTypeValue;

    private String address;

    @Transient
    private String galleryName;

    private double lat;

    private double lon;

    @Transient
    private String imageUrl;

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "venue_space_image_urls",
            joinColumns = @JoinColumn(name = "uuid"))
    @Column(name = "space_image_url")
    private Set<String> spaceImageUrls = new HashSet<>();

    @Transient
    private double relevance;

    //Sharmaneh changes for YMS-44 adding new property for relevance greater than 60%
    @Transient
    private boolean relevant;

    @Transient
    private double distance;

    @Transient
    private List<Tag> tags;

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VenueType getVenueType() {
        return venueType;
    }

    public void setVenueType(VenueType venueType) {
        this.venueType = venueType;
    }

    public int getVenueTypeValue() {
        if (VenueType.UNSPECIFIED.getValue() != venueTypeValue) {
            return venueTypeValue;
        } else {
            return null == venueType ? VenueType.UNSPECIFIED.getValue() : venueType.getValue();
        }
    }

    public void setVenueTypeValue(int venueTypeValue) {
        this.venueTypeValue = venueTypeValue;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof VenueSearchSummary && ((VenueSearchSummary) obj).getUuid().equals(uuid);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }

    public boolean isRelevant() {return relevant;}

    public void setRelevant(boolean relevant) {
        this.relevant = relevant;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getImageUrl() {

        if(null != spaceImageUrls && !spaceImageUrls.isEmpty()){
            imageUrl = spaceImageUrls.iterator().next();
        }
        return imageUrl;

    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGalleryName() {
        return galleryName;
    }

    public void setGalleryName(String galleryName) {
        this.galleryName = galleryName;
    }
}

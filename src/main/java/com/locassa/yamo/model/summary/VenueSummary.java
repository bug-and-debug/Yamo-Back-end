package com.locassa.yamo.model.summary;

import com.locassa.yamo.model.Tag;
import com.locassa.yamo.model.enums.VenueType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "venue")
public class VenueSummary implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uuid;

    private String name;

    @Column(columnDefinition = "VARCHAR(4000)")
    private String description;

    private VenueType venueType;

    @Transient
    private int venueTypeValue;

    @Transient
    private boolean popularVenue = false;

    @ManyToMany
    private Set<Tag> tags;

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

    public boolean isPopularVenue() {
        return popularVenue;
    }

    public void setPopularVenue(boolean popularVenue) {
        this.popularVenue = popularVenue;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof VenueSummary && ((VenueSummary) obj).getUuid().equals(uuid);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}

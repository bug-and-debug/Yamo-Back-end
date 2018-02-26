package com.locassa.yamo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.locassa.yamo.model.enums.VenueType;
import com.locassa.yamo.model.summary.VenueSearchSummary;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

@Entity
@Table(name = "venue")
public class Venue extends AuditEntity implements Serializable {

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

    private double fee;

    @ElementCollection
    @CollectionTable(name = "venue_space_image_urls",
            joinColumns = @JoinColumn(name = "uuid"))
    @Column(name = "space_image_url")
    private Set<String> spaceImageUrls = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "venue_associated_image_urls",
            joinColumns = @JoinColumn(name = "uuid"))
    @Column(name = "associated_image_url")
    private Set<String> associatedImageUrls = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    private Set<User> favouriteUsers = new HashSet<>();

    @Transient
    private String galleryName;

    @Transient
    private int favouriteUsersCount;

    @Transient
    private boolean favouriteUsersTapped;

    @ManyToMany
    @JsonIgnore
    private Set<User> likeUsers = new HashSet<>();

    @Transient
    private int likeUsersCount;

    @Transient
    private boolean likeUsersTapped;

    private String openingTimes;

    private String website;

    private double lat;

    private double lon;

    private String location;

    private Date startDate;

    private Date endDate;

    @ManyToMany
    private Set<Venue> children = new HashSet<>();

    @Transient
    private Set<VenueSearchSummary> recommended = new HashSet<>();

    @ManyToMany
    @OrderBy("priority DESC, name ASC")
    private SortedSet<Tag> tags;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public Set<String> getSpaceImageUrls() {
        return spaceImageUrls;
    }

    public void setSpaceImageUrls(Set<String> spaceImageUrls) {
        this.spaceImageUrls = spaceImageUrls;
    }

    public Set<String> getAssociatedImageUrls() {
        return associatedImageUrls;
    }

    public void setAssociatedImageUrls(Set<String> associatedImageUrls) {
        this.associatedImageUrls = associatedImageUrls;
    }

    public Set<User> getFavouriteUsers() {
        return favouriteUsers;
    }

    public void setFavouriteUsers(Set<User> favouriteUsers) {
        this.favouriteUsers = favouriteUsers;
    }

    public int getFavouriteUsersCount() {
        return null == favouriteUsers ? 0 : favouriteUsers.size();
    }

    public void setFavouriteUsersCount(int favouriteUsersCount) {
        this.favouriteUsersCount = favouriteUsersCount;
    }

    public boolean isFavouriteUsersTapped() {
        return favouriteUsersTapped;
    }

    public void setFavouriteUsersTapped(boolean favouriteUsersTapped) {
        this.favouriteUsersTapped = favouriteUsersTapped;
    }

    public Set<User> getLikeUsers() {
        return likeUsers;
    }

    public void setLikeUsers(Set<User> likeUsers) {
        this.likeUsers = likeUsers;
    }

    public int getLikeUsersCount() {
        return null == likeUsers ? 0 : likeUsers.size();
    }

    public void setLikeUsersCount(int likeUsersCount) {
        this.likeUsersCount = likeUsersCount;
    }

    public boolean isLikeUsersTapped() {
        return likeUsersTapped;
    }

    public void setLikeUsersTapped(boolean likeUsersTapped) {
        this.likeUsersTapped = likeUsersTapped;
    }

    public String getOpeningTimes() {
        return openingTimes;
    }

    public void setOpeningTimes(String openingTimes) {
        this.openingTimes = openingTimes;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Set<Venue> getChildren() {
        return children;
    }

    public void setChildren(Set<Venue> children) {
        this.children = children;
    }

    public Set<VenueSearchSummary> getRecommended() {
        return recommended;
    }

    public void setRecommended(Set<VenueSearchSummary> recommended) {
        this.recommended = recommended;
    }

    public SortedSet<Tag> getTags() {
        return tags;
    }

    public void setTags(SortedSet<Tag> tags) {
        this.tags = tags;
    }

    public String getGalleryName() {
        return galleryName;
    }

    public void setGalleryName(String galleryName) {
        this.galleryName = galleryName;
    }
}

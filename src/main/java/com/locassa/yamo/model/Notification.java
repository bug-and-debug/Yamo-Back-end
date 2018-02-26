package com.locassa.yamo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.locassa.yamo.model.enums.NotificationType;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Notification extends AuditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uuid;

    @ManyToOne
    @JsonIgnore
    private User user;

    private NotificationType type;

    @Transient
    private int typeValue;

    private Long associatedUser;

    private Long associatedVenue;

    @Formula("(select not exists(select nu.uuid from notification_unseen nu where nu.notification_uuid = uuid))")
    private boolean notificationRead;

    private String userText;

    private String venueText;

    private String altText;

    private String associatedUserProfileImageUrl;

    @Transient
    private boolean following;

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public int getTypeValue() {
        if (NotificationType.UNSPECIFIED.getValue() != typeValue) {
            return typeValue;
        } else {
            return null == type ? NotificationType.UNSPECIFIED.getValue() : type.getValue();
        }
    }

    public void setTypeValue(int typeValue) {
        this.typeValue = typeValue;
    }

    public Long getAssociatedUser() {
        return associatedUser;
    }

    public void setAssociatedUser(Long associatedUser) {
        this.associatedUser = associatedUser;
    }

    public Long getAssociatedVenue() {
        return associatedVenue;
    }

    public void setAssociatedVenue(Long associatedVenue) {
        this.associatedVenue = associatedVenue;
    }

    public boolean isNotificationRead() {
        return notificationRead;
    }

    public void setNotificationRead(boolean notificationRead) {
        this.notificationRead = notificationRead;
    }

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }

    public String getVenueText() {
        return venueText;
    }

    public void setVenueText(String venueText) {
        this.venueText = venueText;
    }

    public String getAssociatedUserProfileImageUrl() {
        return associatedUserProfileImageUrl;
    }

    public void setAssociatedUserProfileImageUrl(String associatedUserProfileImageUrl) {
        this.associatedUserProfileImageUrl = associatedUserProfileImageUrl;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }
}

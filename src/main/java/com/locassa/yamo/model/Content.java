package com.locassa.yamo.model;

import com.locassa.yamo.model.enums.ContentType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Content extends AuditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uuid;

    private ContentType contentType;

    @Transient
    private int contentTypeValue;

    private String title;

    private String description;

    private String imageUrl;

    @ManyToOne
    private Medium medium = null;

    @ManyToOne
    private Movement movement = null;

    @ManyToOne
    private Venue venue = null;

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public int getContentTypeValue() {
        if (ContentType.UNSPECIFIED.getValue() != contentTypeValue) {
            return contentTypeValue;
        } else {
            return null == contentType ? ContentType.UNSPECIFIED.getValue() : contentType.getValue();
        }
    }

    public void setContentTypeValue(int contentTypeValue) {
        this.contentTypeValue = contentTypeValue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Medium getMedium() {
        return medium;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    public Movement getMovement() {
        return movement;
    }

    public void setMovement(Movement movement) {
        this.movement = movement;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }
}

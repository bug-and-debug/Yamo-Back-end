package com.locassa.yamo.model.dto;

import java.io.Serializable;

public class CardReplyDTO implements Serializable {

    private int rating;

    private Long artWorkId;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Long getArtWorkId() {
        return artWorkId;
    }

    public void setArtWorkId(Long artWorkId) {
        this.artWorkId = artWorkId;
    }
}

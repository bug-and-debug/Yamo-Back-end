package com.locassa.yamo.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_tag")
public class UserTagRelevance extends AuditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uuid;

    @Column(name = "user_uuid")
    private Long userId;

    @Column(name = "tag_uuid")
    private Long tagId;

    private long weight;

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }
}

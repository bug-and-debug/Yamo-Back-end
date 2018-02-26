package com.locassa.yamo.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.SortedSet;

@Entity
public class TagGroup extends AuditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uuid;

    private String name;

    @ManyToMany
    @OrderBy("name ASC")
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

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(SortedSet<Tag> tags) {
        this.tags = tags;
    }
}

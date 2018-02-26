package com.locassa.yamo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.locassa.yamo.model.enums.NotificationPlatform;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Device extends AuditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne
    private User user;

    private String token;

    private NotificationPlatform platform;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public NotificationPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(NotificationPlatform platform) {
        this.platform = platform;
    }
}

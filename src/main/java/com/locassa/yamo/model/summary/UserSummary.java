package com.locassa.yamo.model.summary;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user")
public class UserSummary implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uuid;

    @Column(name = "email", unique = true, nullable = false)
    @JsonIgnore
    private String email;

    private String profileImageUrl;

    @JsonIgnore
    private String nickname;

    @JsonIgnore
    private boolean nickNameEnabled;

    @JsonIgnore
    private String firstName;

    @JsonIgnore
    private String lastName;

    @Transient
    private String username;

    @JsonIgnore
    private String facebookId;

    @Transient
    private boolean facebookUser;

    private boolean following;

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isNickNameEnabled() {
        return nickNameEnabled;
    }

    public void setNickNameEnabled(boolean nickNameEnabled) {
        this.nickNameEnabled = nickNameEnabled;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return nickNameEnabled ? nickname : String.format("%s %s", firstName, lastName).trim();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public boolean isFacebookUser() {
        return null != facebookId;
    }

    public void setFacebookUser(boolean facebookUser) {
        this.facebookUser = facebookUser;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof UserSummary && ((UserSummary) obj).getUuid().equals(uuid);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }
}

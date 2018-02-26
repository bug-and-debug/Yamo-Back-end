package com.locassa.yamo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.locassa.yamo.model.enums.UserType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
public class User extends AuditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uuid;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    private String password;

    @JsonIgnore
    private String secretCode;

    private boolean enabled;

    private boolean visible;

    private boolean signUpCompleted;

    private boolean ready;

    private String profileImageUrl;

    private String nickname;

    private boolean nickNameEnabled;

    private String firstName;

    private String lastName;

    private String city;

    private double lat;

    private double lon;

    private String location;

    private String facebookId;

    private UserType userType;

    @Transient
    private int userTypeValue;

    @ManyToMany
    @JsonIgnore
    private Set<User> followers = Collections.emptySet();

    @Transient
    private int followersCount;

    @Transient
    private boolean followersTapped;

    @ManyToMany
    @JsonIgnore
    private Set<User> reports = new HashSet<>();

    @Transient
    private int reportsCount;

    @Transient
    private boolean reportsTapped;

    @ManyToMany
    @JsonIgnore
    private Set<Medium> mediums = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    private Set<Movement> movements = new HashSet<>();

    @JsonIgnore
    private boolean following;

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public boolean isSignUpCompleted() {
        return signUpCompleted;
    }

    public void setSignUpCompleted(boolean signUpCompleted) {
        this.signUpCompleted = signUpCompleted;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public int getUserTypeValue() {
        if (UserType.UNSPECIFIED.getValue() != userTypeValue) {
            return userTypeValue;
        } else {
            return null == userType ? UserType.UNSPECIFIED.getValue() : userType.getValue();
        }
    }

    public void setUserTypeValue(int userTypeValue) {
        this.userTypeValue = userTypeValue;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public int getFollowersCount() {
        return null == followers ? 0 : followers.size();
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public boolean isFollowersTapped() {
        return followersTapped;
    }

    public void setFollowersTapped(boolean followersTapped) {
        this.followersTapped = followersTapped;
    }

    public Set<User> getReports() {
        return reports;
    }

    public void setReports(Set<User> reports) {
        this.reports = reports;
    }

    public int getReportsCount() {
        return null == reports ? 0 : reports.size();
    }

    public void setReportsCount(int reportsCount) {
        this.reportsCount = reportsCount;
    }

    public boolean isReportsTapped() {
        return reportsTapped;
    }

    public void setReportsTapped(boolean reportsTapped) {
        this.reportsTapped = reportsTapped;
    }

    public Set<Medium> getMediums() {
        return mediums;
    }

    public void setMediums(Set<Medium> mediums) {
        this.mediums = mediums;
    }

    public Set<Movement> getMovements() {
        return movements;
    }

    public void setMovements(Set<Movement> movements) {
        this.movements = movements;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof User && ((User) obj).getUuid().equals(uuid);
    }

    public String generateFullName() {
        return String.format("%s %s", firstName, lastName).trim();
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }
}

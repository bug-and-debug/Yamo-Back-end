package com.locassa.yamo.model.dto;

import com.locassa.yamo.model.Medium;
import com.locassa.yamo.model.Place;
import com.locassa.yamo.model.summary.RouteSummary;
import com.locassa.yamo.model.summary.UserSummary;
import com.locassa.yamo.model.summary.VenueSummary;

import java.io.Serializable;
import java.util.List;

public class ProfileDTO implements Serializable {

    private String profileImageUrl;

    private String firstName;

    private String lastName;

    private String nickname;

    private boolean nicknameEnabled;

    private String location;

    private List<VenueSummary> venues;

    private List<UserSummary> followers;

    private List<UserSummary> following;

    private List<RouteSummary> routes;

    private List<Medium> mediums;

    private List<Place> places;

    private boolean ownProfile;

    private boolean userFollowing;

    private boolean reported;

    private boolean visible;

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<VenueSummary> getVenues() {
        return venues;
    }

    public void setVenues(List<VenueSummary> venues) {
        this.venues = venues;
    }

    public List<UserSummary> getFollowers() {
        return followers;
    }

    public void setFollowers(List<UserSummary> followers) {
        this.followers = followers;
    }

    public List<RouteSummary> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteSummary> routes) {
        this.routes = routes;
    }

    public List<Medium> getMediums() {
        return mediums;
    }

    public void setMediums(List<Medium> mediums) {
        this.mediums = mediums;
    }

    public boolean isOwnProfile() {
        return ownProfile;
    }

    public void setOwnProfile(boolean ownProfile) {
        this.ownProfile = ownProfile;
    }

    public List<UserSummary> getFollowing() {
        return following;
    }

    public void setFollowing(List<UserSummary> following) {
        this.following = following;
    }

    public boolean isUserFollowing() {
        return userFollowing;
    }

    public void setUserFollowing(boolean userFollowing) {
        this.userFollowing = userFollowing;
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isNicknameEnabled() {
        return nicknameEnabled;
    }

    public void setNicknameEnabled(boolean nicknameEnabled) {
        this.nicknameEnabled = nicknameEnabled;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}

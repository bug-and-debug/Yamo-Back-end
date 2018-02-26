package com.locassa.yamo.model.dto;

import java.io.Serializable;

public class EditProfileDTO implements Serializable {

    private String profileImageContent;

    private String nickname;

    private boolean nickNameEnabled;

    private String firstName;

    private String lastName;

    private String city;

    private boolean visible;

    private boolean signUpCompleted;

    public String getProfileImageContent() {
        return profileImageContent;
    }

    public void setProfileImageContent(String profileImageContent) {
        this.profileImageContent = profileImageContent;
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isSignUpCompleted() {
        return signUpCompleted;
    }

    public void setSignUpCompleted(boolean signUpCompleted) {
        this.signUpCompleted = signUpCompleted;
    }
}

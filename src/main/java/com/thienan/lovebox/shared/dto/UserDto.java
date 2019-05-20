package com.thienan.lovebox.shared.dto;

import java.util.HashSet;
import java.util.Set;

public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String moodMessage;
    private PhotoDto photo;
    private Set<RoleDto> roles = new HashSet<>();
    private Set<UserDto> following = new HashSet<>();
    private Set<UserDto> followers = new HashSet<>();
    private BffDetailDto bffDetail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMoodMessage() {
        return moodMessage;
    }

    public void setMoodMessage(String moodMessage) {
        this.moodMessage = moodMessage;
    }

    public PhotoDto getPhoto() {
        return photo;
    }

    public void setPhoto(PhotoDto photo) {
        this.photo = photo;
    }

    public Set<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDto> roles) {
        this.roles = roles;
    }

    public Set<UserDto> getFollowing() {
        return following;
    }

    public void setFollowing(Set<UserDto> following) {
        this.following = following;
    }

    public Set<UserDto> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<UserDto> followers) {
        this.followers = followers;
    }

    public BffDetailDto getBffDetail() {
        return bffDetail;
    }

    public void setBffDetail(BffDetailDto bffDetail) {
        this.bffDetail = bffDetail;
    }
}

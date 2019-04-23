package com.thienan.lovebox.payload.response;

import java.util.Set;

public class UserDetailResponse {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Set<UserBriefDetailResponse> following;
    private Set<UserBriefDetailResponse> followers;
    private BffDetailResponse bffDetail;

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

    public Set<UserBriefDetailResponse> getFollowing() {
        return following;
    }

    public void setFollowing(Set<UserBriefDetailResponse> following) {
        this.following = following;
    }

    public Set<UserBriefDetailResponse> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<UserBriefDetailResponse> followers) {
        this.followers = followers;
    }

    public BffDetailResponse getBffDetail() {
        return bffDetail;
    }

    public void setBffDetail(BffDetailResponse bffDetail) {
        this.bffDetail = bffDetail;
    }
}

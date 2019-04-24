package com.thienan.lovebox.payload.response;

public class UserDetailResponse {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private int followingCount;
    private int followersCount;
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

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public BffDetailResponse getBffDetail() {
        return bffDetail;
    }

    public void setBffDetail(BffDetailResponse bffDetail) {
        this.bffDetail = bffDetail;
    }
}

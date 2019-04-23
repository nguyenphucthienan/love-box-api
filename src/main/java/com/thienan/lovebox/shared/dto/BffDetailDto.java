package com.thienan.lovebox.shared.dto;

public class BffDetailDto {

    private Long id;
    private UserDto firstUser;
    private UserDto secondUser;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDto getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(UserDto firstUser) {
        this.firstUser = firstUser;
    }

    public UserDto getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(UserDto secondUser) {
        this.secondUser = secondUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

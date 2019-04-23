package com.thienan.lovebox.shared.dto;

public class BffRequestDto {

    private Long id;
    private UserDto fromUser;
    private UserDto toUser;
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDto getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserDto fromUser) {
        this.fromUser = fromUser;
    }

    public UserDto getToUser() {
        return toUser;
    }

    public void setToUser(UserDto toUser) {
        this.toUser = toUser;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

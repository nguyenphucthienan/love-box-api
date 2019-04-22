package com.thienan.lovebox.payload.response;

public class UserAvailabilityResponse {

    private boolean available;

    public UserAvailabilityResponse(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}

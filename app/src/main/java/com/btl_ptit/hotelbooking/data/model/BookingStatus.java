package com.btl_ptit.hotelbooking.data.model;

import java.io.Serializable;

public enum BookingStatus implements Serializable {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled");

    private final String status;

    BookingStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

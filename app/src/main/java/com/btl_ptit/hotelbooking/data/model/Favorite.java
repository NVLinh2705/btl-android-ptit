package com.btl_ptit.hotelbooking.data.model;

import com.google.gson.annotations.SerializedName;

public class Favorite {
    @SerializedName("id")
    private Integer id;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("hotelId") // Đổi sang camelCase để khớp với Edge Function
    private String hotelId;

    @SerializedName("created_at")
    private String createdAt;

    public Favorite() {
    }

    public Favorite(String customerId, String hotelId) {
        this.customerId = customerId;
        this.hotelId = hotelId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

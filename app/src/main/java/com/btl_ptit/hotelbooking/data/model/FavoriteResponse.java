package com.btl_ptit.hotelbooking.data.model;

import com.google.gson.annotations.SerializedName;

public class FavoriteResponse {
    @SerializedName("id")
    private Integer id;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("hotel_id")
    private Integer hotelId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("hotels")
    private MyHotel hotel;

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

    public Integer getHotelId() {
        return hotelId;
    }

    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public MyHotel getHotel() {
        return hotel;
    }

    public void setHotel(MyHotel hotel) {
        this.hotel = hotel;
    }
}

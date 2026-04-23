package com.btl_ptit.hotelbooking.data.dto;


public class LikeHotelRequest {
    private int hotelId;

    public LikeHotelRequest(int hotelId) {
        this.hotelId = hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public int getHotelId() {
        return hotelId;
    }
}

package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class HotelInBoundResponse {
    private String id;
    private String name, avatar, location;
    private double rating;
    @SerializedName("average_price")
    private int averagePrice;
    private float latitude, longitude;
    @SerializedName("is_liked")
    private boolean isLiked;

    public HotelInBoundResponse() {
    }

    public HotelInBoundResponse(String id, String name, String avatar, String location, double rating, int averagePrice, float latitude, float longitude, boolean isLiked) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.location = location;
        this.rating = rating;
        this.averagePrice = averagePrice;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isLiked = isLiked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(int averagePrice) {
        this.averagePrice = averagePrice;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        HotelInBoundResponse myHotel = (HotelInBoundResponse) other;
        return Objects.equals(id, myHotel.getId()) &&
                Objects.equals(name, myHotel.getName()) &&
                Objects.equals(avatar, myHotel.getAvatar()) &&
                Objects.equals(location, myHotel.getLocation()) &&
                Objects.equals(rating, myHotel.getRating()) &&
                Objects.equals(averagePrice, myHotel.getAveragePrice()) &&
                Objects.equals(latitude, myHotel.getLatitude()) &&
                Objects.equals(longitude, myHotel.getLongitude()) &&
                isLiked == myHotel.isLiked;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, avatar, location, rating, averagePrice, latitude, longitude, isLiked);
    }

}

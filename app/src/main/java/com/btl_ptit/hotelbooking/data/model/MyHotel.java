package com.btl_ptit.hotelbooking.data.model;

import com.btl_ptit.hotelbooking.interfaces.DiffUtilModel;

import java.io.Serializable;
import java.util.Objects;

public class MyHotel implements DiffUtilModel, Serializable {
    private String id;
    private String name, avatar, location;
    private double rating;
    private int averagePrice;

    public MyHotel() {
    }

    public MyHotel(String id, String name, String avatar, String location, double rating, int averagePrice) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.location = location;
        this.rating = rating;
        this.averagePrice = averagePrice;
    }

    public int getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(int averagePrice) {
        this.averagePrice = averagePrice;
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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        MyHotel myHotel = (MyHotel) other;
        return Objects.equals(id, myHotel.getId()) &&
                Objects.equals(name, myHotel.getName()) &&
                Objects.equals(avatar, myHotel.getAvatar()) &&
                Objects.equals(location, myHotel.getLocation()) &&
                Objects.equals(rating, myHotel.getRating()) &&
                Objects.equals(averagePrice, myHotel.getAveragePrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, avatar, location, rating, averagePrice);
    }

    @Override
    public Object getUniqueIdentifier() {
        return id;
    }
}

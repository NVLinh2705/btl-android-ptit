package com.btl_ptit.hotelbooking.data.model;

import com.btl_ptit.hotelbooking.interfaces.DiffUtilModel;

import java.util.Objects;

public class MyPopularDestination implements DiffUtilModel {
    private String id;
    private String name, avatar, averagePrice;

    public MyPopularDestination() {
    }

    public MyPopularDestination(String id, String name, String avatar, String averagePrice) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
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

    public String getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(String averagePrice) {
        this.averagePrice = averagePrice;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        MyPopularDestination myPopularDestination = (MyPopularDestination) other;
        return Objects.equals(id, myPopularDestination.getId()) &&
                Objects.equals(name, myPopularDestination.getName()) &&
                Objects.equals(avatar, myPopularDestination.getAvatar()) &&
                Objects.equals(averagePrice, myPopularDestination.getAveragePrice());
    }

    @Override
    public Object getUniqueIdentifier() {
        return id;
    }
}

package com.btl_ptit.hotelbooking.data.model;

import com.btl_ptit.hotelbooking.interfaces.DiffUtilModel;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyHotel implements DiffUtilModel {
    private Integer id;
    private String name, avatar, location;
    private double rating;
    private int averagePrice;

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

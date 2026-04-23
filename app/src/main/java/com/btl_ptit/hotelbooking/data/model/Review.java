package com.btl_ptit.hotelbooking.data.model;


import com.btl_ptit.hotelbooking.interfaces.DiffUtilModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review implements DiffUtilModel, Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("user")
    private User customer;

    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    @SerializedName("hotel_id")
    private String hotelId;

    @SerializedName("created_at")
    private String createdAt;

    @Override
    public Object getUniqueIdentifier() {
        return id;
    }
}

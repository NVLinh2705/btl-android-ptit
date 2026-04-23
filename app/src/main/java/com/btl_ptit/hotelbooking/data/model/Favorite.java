package com.btl_ptit.hotelbooking.data.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Favorite {
    @SerializedName("id")
    private Integer id;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("hotelId") // Đổi sang camelCase để khớp với Edge Function
    private Integer hotelId;

    @SerializedName("created_at")
    private String createdAt;


    public Favorite(String customerId, Integer hotelId) {
        this.customerId = customerId;
        this.hotelId = hotelId;
    }

}

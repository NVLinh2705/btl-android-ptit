package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelImage {
    @SerializedName("id")
    private int id;

    @SerializedName("url")
    private String url;

    @SerializedName("is_cover")
    private boolean isCover;

    @SerializedName("room_type_id")
    private Integer roomTypeId;

    @SerializedName("created_at")
    private String createdAt;

}


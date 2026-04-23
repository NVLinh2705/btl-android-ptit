package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelReview {
    @SerializedName("id")
    private int id;

    @SerializedName("rating")
    private double rating;

    @SerializedName("comment")
    private String comment;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("reviewer")
    private Reviewer reviewer;

    @SerializedName("room_type")
    private RoomTypeInfo roomType;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Reviewer {
        @SerializedName("id")
        private String id;

        @SerializedName("full_name")
        private String fullName;

        @SerializedName("avatar_url")
        private String avatarUrl;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoomTypeInfo {
        @SerializedName("id")
        private Integer id;

        @SerializedName("name")
        private String name;

        @SerializedName("image_url")
        private String imageUrl;
    }
}


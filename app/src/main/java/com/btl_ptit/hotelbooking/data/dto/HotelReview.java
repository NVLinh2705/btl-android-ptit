package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    @SerializedName("customer")
    private Customer customer;

    @SerializedName("booked_rooms")
    private List<RoomTypeInfo> bookedRooms;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Customer {
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
        @SerializedName("room_type_id")
        private Integer id;

        @SerializedName("room_type_name")
        private String roomTypeName;

        @SerializedName("cover_image")
        private String coverImage;
        @SerializedName("nights")
        private Integer nights;
    }
}


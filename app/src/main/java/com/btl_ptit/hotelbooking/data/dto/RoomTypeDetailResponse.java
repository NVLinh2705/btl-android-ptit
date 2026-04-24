package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomTypeDetailResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("hotel_id")
    private int hotelId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("max_guests")
    private int maxGuests;

    @SerializedName("bed_count")
    private int bedCount;

    @SerializedName("bed_type")
    private String bedType;

    @SerializedName("base_price_per_night")
    private double basePricePerNight;

    @SerializedName("has_free_cancellation")
    private boolean hasFreeCancellation;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("area")
    private Integer area;

    @SerializedName("images")
    private List<ImageItem> images;

    @SerializedName("facilities_flat")
    private List<HotelFacility> facilitiesFlat;

    @SerializedName("policies")
    private List<Policy> policies;

//    @SerializedName("reviews")
//    private List<HotelReview> reviews;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageItem {
        @SerializedName("id")
        private int id;

        @SerializedName("url")
        private String url;

        @SerializedName("is_cover")
        private boolean isCover;

        @SerializedName("created_at")
        private String createdAt;
    }
}


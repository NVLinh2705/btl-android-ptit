package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableRoomTypesResponse {

    @SerializedName("hotel_id")
    private int hotelId;

    @SerializedName("checkin")
    private String checkin;

    @SerializedName("checkout")
    private String checkout;

    @SerializedName("nights")
    private int nights;

    @SerializedName("room_quantity")
    private int roomQuantity;

    @SerializedName("adults")
    private int adults;

    @SerializedName("children")
    private int children;

    @SerializedName("data")
    private List<AvailableRoomTypeItem> data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AvailableRoomTypeItem {
        @SerializedName("id")
        private int id;

        @SerializedName("hotel_id")
        private int hotelId;

        @SerializedName("name")
        private String name;

        @SerializedName("max_guests")
        private int maxGuests;

        @SerializedName("bed_count")
        private int bedCount;

        @SerializedName("bed_type")
        private String bedType;

        @SerializedName("area")
        private Double area;

        @SerializedName("view")
        private String view;

        @SerializedName("has_free_cancellation")
        private boolean hasFreeCancellation;

        @SerializedName("base_price_per_night")
        private double basePricePerNight;

        @SerializedName("nights")
        private int nights;

        @SerializedName("total_price")
        private double totalPrice;

        @SerializedName("thumbnail_url")
        private String thumbnailUrl;

        @SerializedName("available_quantity")
        private int availableQuantity;

        @SerializedName("facilities")
        private List<HotelFacility> facilities;

        @SerializedName("policies")
        private List<RoomPolicy> policies;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoomPolicy {
        @SerializedName("id")
        private int id;

        @SerializedName("title")
        private String title;

        @SerializedName("content")
        private String content;

        @SerializedName("type_code")
        private String typeCode;

        @SerializedName("type_name")
        private String typeName;
    }
}


package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelStats {
    @SerializedName("avg_rating")
    private Double avgRating;

    @SerializedName("total_reviews")
    private int totalReviews;

    @SerializedName("total_images")
    private int totalImages;

    @SerializedName("total_facilities")
    private int totalFacilities;

}


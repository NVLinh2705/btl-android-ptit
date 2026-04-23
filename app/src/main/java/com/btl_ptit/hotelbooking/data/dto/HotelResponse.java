package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelResponse extends Hotel {
    @SerializedName("stats")
    private HotelStats stats;

    @SerializedName("facilities")
    private List<HotelFacility> facilities;

    @SerializedName("images")
    private List<HotelImage> images;

    @SerializedName("reviews")
    private List<ReviewSummary> reviews;

    @SerializedName("policies")
    private List<Policy> policies;

    @SerializedName("is_liked")
    private boolean isLiked;

}

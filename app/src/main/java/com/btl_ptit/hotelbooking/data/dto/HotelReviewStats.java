package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelReviewStats {
    @SerializedName("average_rating")
    private Double averageRating;
    @SerializedName("total_reviews")
    private Integer totalReviews;
    @SerializedName("rating_counts")
    Map<String, Integer> ratingCounts;
}

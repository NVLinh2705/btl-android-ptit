package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedReviewsResponse {
    @SerializedName("data")
    private List<HotelReview> data;

    @SerializedName("page")
    private int page;

    @SerializedName("page_size")
    private int pageSize;

    @SerializedName("total")
    private int total;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("has_next")
    private boolean hasNext;

    @SerializedName("has_prev")
    private boolean hasPrev;
}


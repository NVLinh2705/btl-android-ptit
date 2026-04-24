package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

    @SerializedName("booking_id")
    private String bookingId;
    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    @SerializedName("hotel_id")
    private String hotelId;

    @SerializedName("customer_id")
    private String customerId;

}

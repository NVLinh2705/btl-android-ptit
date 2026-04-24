package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingResponse {
    @SerializedName("booking_id")
    private int bookingId;

    @SerializedName("booking_code")
    private String bookingCode;

    @SerializedName("status")
    private String status;

    @SerializedName("total_amount")
    private double totalAmount;
}
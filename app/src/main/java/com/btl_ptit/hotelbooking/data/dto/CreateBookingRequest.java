package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    @SerializedName("p_hotel_id")
    private int hotelId;

    @SerializedName("p_check_in_date")
    private String checkInDate;

    @SerializedName("p_check_out_date")
    private String checkOutDate;

    @SerializedName("p_guest_name")
    private String guestName;

    @SerializedName("p_guest_phone")
    private String guestPhone;

    @SerializedName("p_guest_email")
    private String guestEmail;

    @SerializedName("p_num_adults")
    private int numAdults;

    @SerializedName("p_num_children")
    private int numChildren;

    @SerializedName("p_special_requests")
    private String specialRequests;

    @SerializedName("p_room_selections")
    private List<RoomSelection> roomSelections;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomSelection {
        @SerializedName("room_type_id")
        private int roomTypeId;

        @SerializedName("quantity")
        private int quantity;

        @SerializedName("price_at_booking")
        private double priceAtBooking;
    }
}
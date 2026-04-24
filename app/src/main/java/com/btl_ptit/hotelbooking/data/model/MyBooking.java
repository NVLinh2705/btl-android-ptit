package com.btl_ptit.hotelbooking.data.model;

import com.btl_ptit.hotelbooking.interfaces.DiffUtilModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyBooking implements DiffUtilModel, Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("booking_code")
    private String bookingCode;

    @SerializedName("total_amount")
    private double totalAmount;

    @SerializedName("status_code")
    private String statusCode;

    @SerializedName("checkin_date")
    private String checkinDate;

    @SerializedName("checkout_date")
    private String checkoutDate;

    @SerializedName("num_adults")
    private int numAdults;

    @SerializedName("num_children")
    private int numChildren;

    @SerializedName("customer_fullname")
    private String customerFullname;

    @SerializedName("customer_phone")
    private String customerPhone;

    @SerializedName("customer_email")
    private String customerEmail;

    @SerializedName("created_at")
    private String createdAt;


    private MyHotel hotels;

    @SerializedName("booked_rooms")
    private List<MyBookedRoom> bookedRooms;



    @Override
    public Object getUniqueIdentifier() {
        return id;
    }

    public MyBooking(String id, String bookingCode, double totalAmount, String statusCode) {
        this.id = id;
        this.bookingCode = bookingCode;
        this.totalAmount = totalAmount;
        this.statusCode = statusCode;
    }




}

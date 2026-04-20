package com.btl_ptit.hotelbooking.data.model;

import com.btl_ptit.hotelbooking.interfaces.DiffUtilModel;
import com.google.gson.annotations.SerializedName;

public class MyBookedRoom  {
    @SerializedName("id")
    private String id;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("price_per_night")
    private double pricePerNight;

    @SerializedName("nights")
    private int nights;

    @SerializedName("subtotal")
    private double subTotal;

    @SerializedName("room_type_id")
    private int roomTypeId;

    @SerializedName("booking_id")
    private int bookingId;

    @SerializedName("room_types")
    private MyRoomType roomType;


    public MyBookedRoom() {
    }

    public MyBookedRoom(double pricePerNight, String id, int quantity, int nights, double subTotal, int roomTypeId, int bookingId, MyRoomType roomType) {
        this.pricePerNight = pricePerNight;
        this.id = id;
        this.quantity = quantity;
        this.nights = nights;
        this.subTotal = subTotal;
        this.roomTypeId = roomTypeId;
        this.bookingId = bookingId;
        this.roomType = roomType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getNights() {
        return nights;
    }

    public void setNights(int nights) {
        this.nights = nights;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public MyRoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(MyRoomType roomType) {
        this.roomType = roomType;
    }
}

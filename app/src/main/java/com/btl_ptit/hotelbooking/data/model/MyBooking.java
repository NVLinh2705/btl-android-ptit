package com.btl_ptit.hotelbooking.data.model;

import com.btl_ptit.hotelbooking.interfaces.DiffUtilModel;
import com.google.gson.annotations.SerializedName;

public class MyBooking implements DiffUtilModel {

    @SerializedName("id")
    private String id;

    @SerializedName("booking_code")
    private String bookingCode;

    @SerializedName("total_amount")
    private double totalAmount;

    @SerializedName("status_code")
    private String statusCode;

    // Lưu ý: JSON trả về hotel_id (số), không phải object hotel.
    // Nếu muốn hiển thị tên khách sạn, bạn cần API trả về nested object hoặc map thủ công.
    private MyHotel hotel;

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

    public MyBooking() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public MyHotel getHotel() {
        return hotel;
    }

    public void setHotel(MyHotel hotel) {
        this.hotel = hotel;
    }
}

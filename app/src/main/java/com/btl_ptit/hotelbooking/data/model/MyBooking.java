package com.btl_ptit.hotelbooking.data.model;

import com.btl_ptit.hotelbooking.interfaces.DiffUtilModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyBooking implements DiffUtilModel {

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




    // Lưu ý: JSON trả về hotel_id (số), không phải object hotel.
    // Nếu muốn hiển thị tên khách sạn, bạn cần API trả về nested object hoặc map thủ công.
    private MyHotel hotels;

    @SerializedName("booked_rooms")
    private List<MyBookedRoom> bookedRooms;

    public List<MyBookedRoom> getBookedRooms() {
        return bookedRooms;
    }

    public void setBookedRooms(List<MyBookedRoom> bookedRooms) {
        this.bookedRooms = bookedRooms;
    }

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

    public MyHotel getHotels() {
        return hotels;
    }

    public void setHotels(MyHotel hotels) {
        this.hotels = hotels;
    }

    public String getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(String checkinDate) {
        this.checkinDate = checkinDate;
    }

    public String getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(String checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public int getNumAdults() {
        return numAdults;
    }

    public void setNumAdults(int numAdults) {
        this.numAdults = numAdults;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }

    public String getCustomerFullname() {
        return customerFullname;
    }

    public void setCustomerFullname(String customerFullname) {
        this.customerFullname = customerFullname;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}

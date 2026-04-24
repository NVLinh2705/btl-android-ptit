package com.btl_ptit.hotelbooking.data.model;

import com.google.gson.annotations.SerializedName;

public class MyNotification {
    private String id;
    private String title;
    private String content;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("customer_id")
    private String customerId;
    private String type; // e.g., "booking_confirmed", "promotion"

    public MyNotification() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}

package com.btl_ptit.hotelbooking.data.model;

import com.google.gson.annotations.SerializedName;

public class User {
    // Adjust these field names/annotations to match your `public.users` columns

    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName(value = "full_name")
    private String fullName;

    @SerializedName(value = "avatar_url")
    private String avatarUrl;

    @SerializedName(value = "is_active")
    private boolean isActive;

    @SerializedName(value = "created_at")
    private String createdAt;

    // Required for Gson
    public User() {
    }

    public User(String email, String phone, String fullName, String avatarUrl, boolean isActive, String createdAt) {
        this.email = email;
        this.phone = phone;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public User(String id, String email, String phone, String fullName, String avatarUrl) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

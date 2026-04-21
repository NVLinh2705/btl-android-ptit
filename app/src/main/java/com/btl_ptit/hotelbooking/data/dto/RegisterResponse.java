package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

    private String id;
    private String email;

    @SerializedName("confirmation_sent_at")
    private String confirmationSentAt;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    private UserMetadata user_metadata;

    // Getter & Setter

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getConfirmationSentAt() {
        return confirmationSentAt;
    }

    public UserMetadata getUser_metadata() {
        return user_metadata;
    }

    // inner class
    public static class UserMetadata {
        private String email;

        @SerializedName("email_verified")
        private boolean emailVerified;

        public String getEmail() {
            return email;
        }

        public boolean isEmailVerified() {
            return emailVerified;
        }
    }
}
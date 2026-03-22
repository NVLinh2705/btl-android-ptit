package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;


public class LoginResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("expires_in")
    private long expiresIn;

    @SerializedName("user")
    private SupabaseUser user;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public SupabaseUser getUser() {
        return user;
    }

    public static class SupabaseUser {
        @SerializedName("id")
        private String id;

        @SerializedName("email")
        private String email;

        @SerializedName("user_metadata")
        private UserMetadata userMetadata;

        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public UserMetadata getUserMetadata() {
            return userMetadata;
        }
    }

    public static class UserMetadata {
        // Common Supabase metadata keys (can differ based on provider)
        @SerializedName("full_name")
        private String fullName;

        @SerializedName("avatar_url")
        private String avatarUrl;

        @SerializedName("name")
        private String name;

        @SerializedName("picture")
        private String picture;

        public String getResolvedFullName() {
            if (fullName != null && !fullName.trim().isEmpty()) return fullName;
            if (name != null && !name.trim().isEmpty()) return name;
            return null;
        }

        public String getResolvedAvatarUrl() {
            if (avatarUrl != null && !avatarUrl.trim().isEmpty()) return avatarUrl;
            if (picture != null && !picture.trim().isEmpty()) return picture;
            return null;
        }
    }
}

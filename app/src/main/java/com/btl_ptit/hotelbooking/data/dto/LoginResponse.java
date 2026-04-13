package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("expires_in")
    private long expiresIn;

    @SerializedName("user")
    private SupabaseUser user;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SupabaseUser {
        @SerializedName("id")
        private String id;

        @SerializedName("email")
        private String email;

        @SerializedName("user_metadata")
        private UserMetadata userMetadata;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
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

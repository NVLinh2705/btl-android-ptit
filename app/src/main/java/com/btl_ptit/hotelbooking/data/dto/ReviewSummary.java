package com.btl_ptit.hotelbooking.data.dto;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSummary {
    @SerializedName("id")
    private int id;

    @SerializedName("rating")
    private int rating;
    

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("comment")
    private String comment;

    private Reviewer reviewer;

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getReviewerFullName() {
        return reviewer != null && reviewer.getFullName() != null ? reviewer.getFullName() : "Anonymous";
    }

    public String getReviewerAvatarUrl() {
        return reviewer != null ? reviewer.getAvatarUrl() : null;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Reviewer {
        @SerializedName("id")
        private String id;

        @SerializedName("full_name")
        private String fullName;

        @SerializedName("avatar_url")
        private String avatarUrl;
    }
}

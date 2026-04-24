package com.btl_ptit.hotelbooking.data.remote.api_services;

import com.btl_ptit.hotelbooking.data.dto.ReviewRequest;
import com.btl_ptit.hotelbooking.data.model.Review;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ReviewRestService {

    @Headers("Auth: True")
    @GET("rest/v1/reviews")
    Single<List<Review>> getTopReviews(
            @Query("select") String select,
            @Query("hotel_id") String hotelId,
            @Query("order") String order,
            @Query("limit") int limit
    );

    @Headers("Auth: True")
    @GET("rest/v1/reviews")
    Single<List<Review>> getAllReviews(
            @Query("select") String select,
            @Query("hotel_id") String hotelId,
            @Query("order") String order,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @Headers("Auth: True")
    @GET("rest/v1/reviews")
    Single<List<Review>> getReviewsByBooking(
            @Query("select") String select,
            @Query("booking_id") String bookingId
    );


    @Headers({
            "Prefer: resolution=merge-duplicates",
            "Prefer: return=representation",
            "Auth: True"
    })
    @POST("rest/v1/reviews")
    Single<List<Review>> upsertReview(
            @Query("on_conflict") String onConflict,
            @Body ReviewRequest request
    );

    @Headers("Auth: True")
    @DELETE("rest/v1/reviews")
    Single<Response<Void>> deleteReviewByBooking(
            @Query("booking_id") String bookingId
    );


}

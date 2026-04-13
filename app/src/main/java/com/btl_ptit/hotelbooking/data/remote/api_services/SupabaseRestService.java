package com.btl_ptit.hotelbooking.data.remote.api_services;

import com.btl_ptit.hotelbooking.data.dto.HotelResponse;
import com.btl_ptit.hotelbooking.data.dto.HotelFacility;
import com.btl_ptit.hotelbooking.data.dto.HotelReview;
import com.btl_ptit.hotelbooking.data.dto.LikeHotelRequest;
import com.btl_ptit.hotelbooking.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseRestService {

    // Example query:
    // GET /rest/v1/users?email=eq.test@gmail.com&select=email,phone,full_name,avatar_url
    @GET("rest/v1/users")
    Call<List<User>> getUserByEmail(
        @Header("Authorization") String bearerToken,
        @Query("email") String emailEq,
        @Query("select") String select
    );

    // Preferred query (secure):
    // GET /rest/v1/users?id=eq.<auth-uuid>&select=email,phone,full_name,avatar_url
    @GET("rest/v1/users")
    Call<List<User>> getUserById(
        @Header("Authorization") String bearerToken,
        @Query("id") String idEq,
        @Query("select") String select
    );

    @GET("functions/v1/get-hotel")
    Call<HotelResponse> getHotelById(
        @Query("hotel_id") int hotelId
    );

    @POST("functions/v1/like-hotel") // bearer token is passed via interceptor
    Call<Void> likeHotel(
        @Body LikeHotelRequest request
    );

    @GET("rest/v1/facilities")
    Call<List<HotelFacility>> getHotelFacilities(
        @Query("hotel_id") int hotelId
    );

    @GET("rest/v1/reviews")
    Call<List<HotelReview>> getHotelReviews(
        @Query("hotel_id") int hotelId,
        @Query("order") String order,
        @Query("limit") int limit,
        @Query("offset") int offset
    );

}

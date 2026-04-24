package com.btl_ptit.hotelbooking.data.remote.api_services;

import com.btl_ptit.hotelbooking.data.dto.HotelResponse;
import com.btl_ptit.hotelbooking.data.dto.HotelFacility;
import com.btl_ptit.hotelbooking.data.dto.HotelReview;
import com.btl_ptit.hotelbooking.data.dto.HotelReviewStats;
import com.btl_ptit.hotelbooking.data.dto.LikeHotelRequest;
import com.btl_ptit.hotelbooking.data.dto.PaginatedReviewsResponse;
import com.btl_ptit.hotelbooking.data.dto.AvailableRoomTypesResponse;
import com.btl_ptit.hotelbooking.data.dto.RoomTypeDetailResponse;
import com.btl_ptit.hotelbooking.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
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

    @GET("rest/v1/hotels")
    Call<List<HotelResponse>> getAllHotels(
        @Header("Authorization") String bearerToken,
        @Query("select") String select
    );

    @GET("functions/v1/get-hotel")
    Call<HotelResponse> getHotelById(
        @Query("hotel_id") int hotelId
    );

    @Headers("Auth: True")
    @POST("functions/v1/like-hotel") // bearer token is passed via interceptor
    Call<Void> likeHotel(
        @Body LikeHotelRequest request
    );

    @GET("functions/v1/get-hotel-facilities")
    Call<List<HotelFacility>> getHotelFacilities(
        @Query("hotel_id") int hotelId
    );

    @GET("rest/v1/rpc/get_hotel_reviews")
    Call<PaginatedReviewsResponse> getHotelReviews(
            @Query("p_hotel_id") int hotelId,
            @Query("p_order") String order,
            @Query("p_page") int page,
//            @Query("p_page_size") int pageSize,
            @Query("p_min_rating") Integer minRating,
            @Query("p_max_rating") Integer maxRating,
            @Query("p_begin_date") String beginDate,
            @Query("p_end_date") String endDate,
            @Query("p_keywords") String keywords
//            @Query("query") String query
    );

    @GET("rest/v1/rpc/get_hotel_reviews_stats")
    Call<HotelReviewStats> getHotelReviewStats(
            @Query("p_hotel_id") int hotelId
    );
    @GET("functions/v1/get-available-room-types")
    Call<AvailableRoomTypesResponse> getAvailableRoomTypes(
            @Query("hotel_id") int hotelId,
            @Query("checkin") String checkin,
            @Query("checkout") String checkout,
            @Query("room_quantity") int roomQuantity,
            @Query("adults") int adults,
            @Query("children") int children
    );

    @GET("functions/v1/get-room-type-detail")
    Call<RoomTypeDetailResponse> getRoomTypeDetail(
            @Query("room_type_id") int roomTypeId
    );

}

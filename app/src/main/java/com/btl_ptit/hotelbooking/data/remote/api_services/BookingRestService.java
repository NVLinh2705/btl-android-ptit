package com.btl_ptit.hotelbooking.data.remote.api_services;

import com.btl_ptit.hotelbooking.data.dto.CreateBookingRequest;
import com.btl_ptit.hotelbooking.data.dto.CreateBookingResponse;
import com.btl_ptit.hotelbooking.data.model.MyBooking;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingRestService {

    @Headers("Auth: True")
    @GET("rest/v1/bookings")
    Single<List<MyBooking>> getListBooking(
            @Query("select") String select,
            @Query("customer_id") String customerId,
            @Query("status_code") String statusCode,
            @Query("order") String order,
            @Query("limit") int limit,
            @Query("offset") int offset
    );
    @Headers("Auth: True")
    @GET("rest/v1/bookings")
    Single<List<MyBooking>> getBookingDetail(
            @Query("id") String id,
            @Query("select") String select
    );

    @Headers("Auth: True")
    @POST("rest/v1/rpc/create_booking")
    Call<CreateBookingResponse> createBooking(
            @Body CreateBookingRequest request
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    @PATCH("rest/v1/bookings")
    Single<Response<List<MyBooking>>> changeStatusBooking(
            @Query("id") String id,
            @Body Map<String, Object> body
    );
}

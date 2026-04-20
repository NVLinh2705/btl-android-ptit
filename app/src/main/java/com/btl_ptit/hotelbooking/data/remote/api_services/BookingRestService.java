package com.btl_ptit.hotelbooking.data.remote.api_services;

import com.btl_ptit.hotelbooking.data.model.MyBooking;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingRestService {

    @Headers("Auth: True")
    @GET("rest/v1/bookings")
    Single<List<MyBooking>> getListBooking(
            @Query("select") String select,
            @Query("limit") int limit,
            @Query("offset") int offset
    );
    @Headers("Auth: True")
    @GET("rest/v1/bookings")
    Single<List<MyBooking>> getBookingDetail(
            @Query("id") String id,
            @Query("select") String select
    );
}

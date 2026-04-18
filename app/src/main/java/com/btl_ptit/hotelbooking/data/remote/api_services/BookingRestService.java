package com.btl_ptit.hotelbooking.data.remote.api_services;

import com.btl_ptit.hotelbooking.data.model.MyBooking;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BookingRestService {

    @GET("rest/v1/bookings")
    Single<List<MyBooking>> getListBooking(
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}

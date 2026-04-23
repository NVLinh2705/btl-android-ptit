package com.btl_ptit.hotelbooking.data.remote.api_services;

import com.btl_ptit.hotelbooking.data.dto.HotelInBoundResponse;
import com.btl_ptit.hotelbooking.data.model.MyHotel;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HotelRestService {
    @GET("hotels")
    Single<List<MyHotel>> getListHotel(
        @Query("page") Integer page,
        @Query("limit") Integer limit
    );

    @GET("hotels/nearby")
    Single<List<HotelInBoundResponse>> getHotelsInBounds(
        @Query("sw_lat") double swLat,
        @Query("sw_lng") double swLng,
        @Query("ne_lat") double neLat,
        @Query("ne_lng") double neLng,
        @Query("zoom") float zoomLevel
    );
}

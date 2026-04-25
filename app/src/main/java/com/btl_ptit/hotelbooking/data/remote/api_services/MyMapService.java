package com.btl_ptit.hotelbooking.data.remote.api_services;

import com.btl_ptit.hotelbooking.data.dto.HotelInBoundResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface MyMapService {
    @Headers("Auth: True")
    @GET("rest/v1/rpc/get_hotels_in_bounds")
    Single<List<HotelInBoundResponse>> getHotelsInBounds(
        @Query("sw_lat") double swLat,
        @Query("sw_lng") double swLng,
        @Query("ne_lat") double neLat,
        @Query("ne_lng") double neLng,
        @Query("zoom_level") float zoomLevel,
        @Query("page_num") Integer page,
        @Query("page_size") Integer limit,
        @Query("p_user_id") String userId
    );
}

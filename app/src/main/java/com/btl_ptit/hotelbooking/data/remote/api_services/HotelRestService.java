package com.btl_ptit.hotelbooking.data.remote.api_services;

import com.btl_ptit.hotelbooking.data.dto.HotelInBoundResponse;
import com.btl_ptit.hotelbooking.data.model.MyHotel;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface HotelRestService {
//    @GET("hotels")
//    Single<List<MyHotel>> getListHotel(
//        @Query("page") Integer page,
//        @Query("limit") Integer limit
//    );

    @Headers("Auth: True")
    @GET("rest/v1/rpc/get_hotel_list_v2")
    Single<List<MyHotel>> getListHotel(
        @Query("page_num") int page,
        @Query("page_size") int limit
    );

    @Headers("Auth: True")
    @GET("rest/v1/rpc/search_hotels_by_num_person")
    Single<List<MyHotel>> searchHotelsByNumPerson(
        @Query("p_province") String province,
        @Query("p_district") String district, // Có thể truyền null hoặc ""
        @Query("p_checkin_date") String checkin,
        @Query("p_checkout_date") String checkout,
        @Query("p_num_room") int numRoom,
        @Query("p_num_adult") int numAdult,
        @Query("p_num_children") int numChildren,
        @Query("page_num") int page,
        @Query("page_size") int limit
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

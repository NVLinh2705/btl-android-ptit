package com.btl_ptit.hotelbooking.data.remote.api_services;

import com.btl_ptit.hotelbooking.data.model.MyPopularDestination;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface MyDestinationRestService {
    @Headers("Auth: True")
    @GET("rest/v1/rpc/get_trending_destinations")
    Single<List<MyPopularDestination>> getListPopularDestination(
        @Query("page_num") int page,
        @Query("page_size") int limit
    );
}

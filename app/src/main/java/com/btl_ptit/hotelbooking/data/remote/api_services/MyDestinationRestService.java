package com.btl_ptit.hotelbooking.data.remote.api_services;

import com.btl_ptit.hotelbooking.data.model.MyPopularDestination;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MyDestinationRestService {
    @GET("destinations")
    Single<List<MyPopularDestination>> getListPopularDestination(
        @Query("page") Integer page,
        @Query("limit") Integer limit
    );
}

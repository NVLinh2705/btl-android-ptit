package com.btl_ptit.hotelbooking.data.remote.api_services;

import com.btl_ptit.hotelbooking.data.model.MyNotification;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NotificationRestService {
    @GET("rest/v1/notifications")
    Single<List<MyNotification>> getNotifications(
            @Query("customer_id") String customerIdEq,
            @Query("select") String select,
            @Query("order") String orderBy
    );
}

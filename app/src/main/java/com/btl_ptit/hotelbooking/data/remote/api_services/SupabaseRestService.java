package com.btl_ptit.hotelbooking.data.remote.api_services;

import com.btl_ptit.hotelbooking.data.model.Favorite;
import com.btl_ptit.hotelbooking.data.model.FavoriteResponse;
import com.btl_ptit.hotelbooking.data.model.User;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseRestService {

    @GET("rest/v1/users")
    Call<List<User>> getUserByEmail(
        @Header("Authorization") String bearerToken,
        @Query("email") String emailEq,
        @Query("select") String select
    );

    @GET("rest/v1/users")
    Call<List<User>> getUserById(
        @Header("Authorization") String bearerToken,
        @Query("id") String idEq,
        @Query("select") String select
    );

    @POST("rest/v1/users")
    Call<Void> insertUser(
        @Header("Authorization") String bearerToken,
        @Body User user
    );

    @PATCH("rest/v1/users")
    Call<Void> updateUser(
        @Header("Authorization") String bearerToken,
        @Query("id") String idEq,
        @Body User user
    );
    @Headers("Auth: True")
    @POST("functions/v1/like-hotel")
    Single<Void> toggleLikeHotel(
        @Body Favorite favoriteRequest
    );

    @Headers("Auth: True")
    @GET("rest/v1/favorites")
    Single<List<FavoriteResponse>> getFavorites(
        @Query("customer_id") String customerIdEq,
        @Query("select") String select
    );
}

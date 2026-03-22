package com.btl_ptit.hotelbooking.data.remote;

import com.btl_ptit.hotelbooking.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SupabaseRestService {

    // Example query:
    // GET /rest/v1/users?email=eq.test@gmail.com&select=email,phone,full_name,avatar_url
    @GET("rest/v1/users")
    Call<List<User>> getUserByEmail(
        @Header("Authorization") String bearerToken,
        @Query("email") String emailEq,
        @Query("select") String select
    );

    // Preferred query (secure):
    // GET /rest/v1/users?id=eq.<auth-uuid>&select=email,phone,full_name,avatar_url
    @GET("rest/v1/users")
    Call<List<User>> getUserById(
        @Header("Authorization") String bearerToken,
        @Query("id") String idEq,
        @Query("select") String select
    );
}

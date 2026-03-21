package com.btl_ptit.hotelbooking.data.remote;

import com.btl_ptit.hotelbooking.data.model.LoginRequest;
import com.btl_ptit.hotelbooking.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseAuthService {
    @POST("auth/v1/token")
    Call<LoginResponse> signIn(
        @Query("grant_type") String grantType,
        @Body LoginRequest request
    );
}


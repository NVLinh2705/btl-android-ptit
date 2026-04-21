package com.btl_ptit.hotelbooking.data.remote.api_services;

import com.btl_ptit.hotelbooking.data.dto.LoginRequest;
import com.btl_ptit.hotelbooking.data.dto.LoginResponse;
import com.btl_ptit.hotelbooking.data.dto.RegisterRequest;
import com.btl_ptit.hotelbooking.data.dto.RegisterResponse;

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

    @POST("auth/v1/signup")
    Call<RegisterResponse> signUp(
            @Query("redirect_to") String redirectTo,
            @Body RegisterRequest request);
}

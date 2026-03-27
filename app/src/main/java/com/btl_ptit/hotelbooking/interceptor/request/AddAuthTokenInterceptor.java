package com.btl_ptit.hotelbooking.interceptor.request;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.btl_ptit.hotelbooking.BuildConfig;
import com.btl_ptit.hotelbooking.MyApplication;
import com.btl_ptit.hotelbooking.utils.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddAuthTokenInterceptor implements Interceptor {

    private final SharedPreferences sharedPreferences;

    public AddAuthTokenInterceptor() {
        this.sharedPreferences = MyApplication.getAppContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder requestWithToken = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("apikey", BuildConfig.SUPABASE_KEY);

        // 1. Lấy Header Authorization mà truyền từ LoginActivity (@Header), nếu ko có thì gọi từ sharedPreferences
        String authHeader = original.header("Authorization");
        if (authHeader != null && !authHeader.trim().isEmpty()) {
            requestWithToken.header("Authorization", authHeader);
        }
//        else {
//            String accessToken = this.sharedPreferences.getString(Constants.KEY_ACCESS_TOKEN, null);
//            if (accessToken != null && !accessToken.trim().isEmpty()) {
//                requestWithToken.header("Authorization", accessToken);
//            }
//        }

        return chain.proceed(requestWithToken.build());
    }

}

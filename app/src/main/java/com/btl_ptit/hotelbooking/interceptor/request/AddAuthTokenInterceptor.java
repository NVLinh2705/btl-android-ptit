package com.btl_ptit.hotelbooking.interceptor.request;

import androidx.annotation.NonNull;

import com.btl_ptit.hotelbooking.BuildConfig;
import com.btl_ptit.hotelbooking.data.session.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddAuthTokenInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder requestWithToken = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("apikey", BuildConfig.SUPABASE_KEY);

        String authFlag = original.header("Auth");
        if (authFlag != null && "true".equalsIgnoreCase(authFlag.trim())) {
            requestWithToken.removeHeader("Auth");
            String accessToken = SessionManager.getInstance().getAccessToken();
            if(accessToken == null || accessToken.trim().isEmpty()) {
                // exception
                throw new IOException("Access token is missing for authenticated request");
            }
            requestWithToken.addHeader("Authorization", "Bearer " + accessToken);
        }

        return chain.proceed(requestWithToken.build());
    }


}

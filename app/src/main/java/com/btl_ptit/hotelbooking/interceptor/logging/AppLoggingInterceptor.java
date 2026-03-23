package com.btl_ptit.hotelbooking.interceptor.logging;

import android.util.Log;

import androidx.annotation.NonNull;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class AppLoggingInterceptor implements Interceptor {

    private final HttpLoggingInterceptor loggingInterceptor;

    public AppLoggingInterceptor(boolean isDebug) {

        loggingInterceptor = new HttpLoggingInterceptor(
                message -> Log.d("API_LOG", message)
        );

        if (isDebug) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        return loggingInterceptor.intercept(chain);
    }
}

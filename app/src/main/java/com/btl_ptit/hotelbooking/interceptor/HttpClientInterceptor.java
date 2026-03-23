package com.btl_ptit.hotelbooking.interceptor;

import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.interceptor.logging.AppLoggingInterceptor;
import com.btl_ptit.hotelbooking.interceptor.request.AddAuthTokenInterceptor;
import com.btl_ptit.hotelbooking.utils.Constants;

import okhttp3.OkHttpClient;

public class HttpClientInterceptor {
    private static volatile OkHttpClient client;

    public static OkHttpClient getClient() {
        if (client == null) {
            synchronized (SupabaseClient.class) {
                if (client == null) {

                    client = new OkHttpClient.Builder()
                            .addInterceptor(new AddAuthTokenInterceptor())
                            .addInterceptor(new AppLoggingInterceptor(Constants.LOG_ENABLED))
                            .build();
                }
            }
        }
        return client;
    }
}

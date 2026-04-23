package com.btl_ptit.hotelbooking.data.remote;

import com.btl_ptit.hotelbooking.interceptor.HttpClientInterceptor;
import com.btl_ptit.hotelbooking.interceptor.request.AddAuthTokenInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseClient {

    // keys are managed in local.properties
    private static final String BASE_URL = com.btl_ptit.hotelbooking.BuildConfig.SUPABASE_URL;
    private static final String API_KEY = com.btl_ptit.hotelbooking.BuildConfig.SUPABASE_KEY;

    private static volatile Retrofit retrofit;

    private static Retrofit getRetrofit() {
        if(retrofit == null) {
            synchronized (SupabaseClient.class) {
                if(retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(HttpClientInterceptor.getClient())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static <T> T createService(Class<T> serviceClass) {
        return getRetrofit().create(serviceClass);
    }


}



package com.btl_ptit.hotelbooking.data.remote;

import com.btl_ptit.hotelbooking.api_parsing.MyGsonConverterFactory;
import com.btl_ptit.hotelbooking.interceptor.HttpClientInterceptor;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

public class MyLocationServiceClient {
    private static final String BASE_URL = "https://provinces.open-api.vn/api/v2/";
    private static volatile Retrofit retrofit;

    private static Retrofit getRetrofit() {
        if(retrofit == null) {
            synchronized (MockApiClient.class) {
                if(retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(HttpClientInterceptor.getClient())
                            .addConverterFactory(MyGsonConverterFactory.getMyGsonConverterFactory())
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

package com.btl_ptit.hotelbooking.data.remote;

import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.api_parsing.HotelListDeserializer;
import com.btl_ptit.hotelbooking.interceptor.HttpClientInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MockApiClient {
    private static final String BASE_URL = "https://69c10873085e1a9fae3fd076.mockapi.io/api/android-ptit/";
    private static volatile Retrofit retrofit;

    private static Retrofit getRetrofit() {
        if(retrofit == null) {
            synchronized (MockApiClient.class) {
                if(retrofit == null) {
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(new TypeToken<List<MyHotel>>(){}.getType(), new HotelListDeserializer())
                            .create();
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(HttpClientInterceptor.getClient())
                            .addConverterFactory(GsonConverterFactory.create(gson))
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

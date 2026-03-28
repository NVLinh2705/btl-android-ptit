package com.btl_ptit.hotelbooking.api_parsing;

import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.data.model.MyPopularDestination;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.converter.gson.GsonConverterFactory;

public class MyGsonConverterFactory {

    private static volatile GsonConverterFactory myGsonConverterFactory;

    public static GsonConverterFactory getMyGsonConverterFactory() {
        if (myGsonConverterFactory == null) {
            synchronized (MyGsonConverterFactory.class) {
                if (myGsonConverterFactory == null) {
                    Gson gson = new GsonBuilder()
                        .registerTypeAdapter(new TypeToken<List<MyHotel>>(){}.getType(), new HotelListDeserializer())
                        .registerTypeAdapter(new TypeToken<List<MyPopularDestination>>(){}.getType(), new PopularDestinationDeserializer())
                        .create();

                    myGsonConverterFactory = GsonConverterFactory.create(gson);
                }
            }
        }
        return myGsonConverterFactory;
    }
}

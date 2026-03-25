package com.btl_ptit.hotelbooking.api_parsing;

import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HotelListDeserializer implements JsonDeserializer<List<MyHotel>> {
    @Override
    public List<MyHotel> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        List<MyHotel> hotels = new ArrayList<>();
        JsonArray array;

        if (json.isJsonArray()) {
            array = json.getAsJsonArray();
        } else if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            array = obj.getAsJsonArray("data");
        } else {
            return hotels;
        }

        if (array != null) {
            for (JsonElement element : array) {
                MyHotel item = context.deserialize(element, MyHotel.class);
                hotels.add(item);
            }
        }

        return hotels;
    }
}

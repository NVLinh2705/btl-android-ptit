package com.btl_ptit.hotelbooking.api_parsing;

import com.btl_ptit.hotelbooking.data.dto.HotelInBoundResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HotelInBoundDeserializer implements JsonDeserializer<List<HotelInBoundResponse>> {
    @Override
    public List<HotelInBoundResponse> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<HotelInBoundResponse> hotels = new ArrayList<>();
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
                HotelInBoundResponse item = context.deserialize(element, HotelInBoundResponse.class);
                hotels.add(item);
            }
        }
        return hotels;
    }
}

package com.btl_ptit.hotelbooking.api_parsing;

import com.btl_ptit.hotelbooking.data.model.MyPopularDestination;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PopularDestinationDeserializer implements JsonDeserializer<List<MyPopularDestination>> {

    @Override
    public List<MyPopularDestination> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<MyPopularDestination> popularDestinations = new ArrayList<>();
        JsonArray array;

        if (json.isJsonArray()) {
            array = json.getAsJsonArray();
        } else if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            array = obj.getAsJsonArray("data");
        } else {
            return popularDestinations;
        }

        if (array != null) {
            for (JsonElement element : array) {
                MyPopularDestination item = context.deserialize(element, MyPopularDestination.class);
                popularDestinations.add(item);
            }
        }

        return popularDestinations;
    }
}

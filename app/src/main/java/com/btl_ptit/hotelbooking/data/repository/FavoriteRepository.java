package com.btl_ptit.hotelbooking.data.repository;

import com.btl_ptit.hotelbooking.data.model.Favorite;
import com.btl_ptit.hotelbooking.data.model.FavoriteResponse;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.SupabaseRestService;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class FavoriteRepository {
    private final SupabaseRestService apiService;

    public FavoriteRepository() {
        this.apiService = SupabaseClient.createService(SupabaseRestService.class);
    }

    public Single<Void> toggleLikeHotel(String userId, String hotelId) {
        return apiService.toggleLikeHotel(new Favorite(userId, hotelId));
    }

    public Single<List<FavoriteResponse>> getFavoriteHotels(String userId) {
        return apiService.getFavorites("eq." + userId, "*,hotels(*)");
    }
}

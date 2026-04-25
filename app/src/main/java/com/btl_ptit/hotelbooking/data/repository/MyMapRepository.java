package com.btl_ptit.hotelbooking.data.repository;

import android.util.Log;

import com.btl_ptit.hotelbooking.data.dto.HotelInBoundResponse;
import com.btl_ptit.hotelbooking.data.remote.api_services.MyMapService;
import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyMapRepository {
    private final MyMapService myMapService;
    public MyMapRepository(MyMapService myMapService) {
        this.myMapService = myMapService;
    }

    public Single<List<HotelInBoundResponse>> fetchHotelsInBounds(LatLngBounds bounds, float zoom, int page, int limit) {
        Log.d("MyMapActivityTAG", "fetchHotelsInBounds called: " + bounds.toString() + " zoom level: " + zoom + " page: " + page + " limit: " + limit);
        return myMapService.getHotelsInBounds(
                bounds.southwest.latitude, bounds.southwest.longitude,
                bounds.northeast.latitude, bounds.northeast.longitude, zoom, page, limit, SessionManager.getInstance().getUser().getId()
        ).subscribeOn(Schedulers.io());
    }
}

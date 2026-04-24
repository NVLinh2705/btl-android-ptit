package com.btl_ptit.hotelbooking.data.repository;

import com.btl_ptit.hotelbooking.data.dto.Province;
import com.btl_ptit.hotelbooking.data.remote.api_services.MyLocationService;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class MyLocationRepository {
    private final MyLocationService service;

    public MyLocationRepository(MyLocationService service) {
        this.service = service;
    }

    public Single<List<Province>> fetchAllProvinces() {
        return service.getProvinces();
    }

    public Single<Province> fetchProvinceDetail(int code) {
        return service.getDistricts(code);
    }
}

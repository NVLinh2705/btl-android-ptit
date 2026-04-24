package com.btl_ptit.hotelbooking.data.remote.api_services;

import com.btl_ptit.hotelbooking.data.dto.Province;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MyLocationService {
    @GET("p/")
    Single<List<Province>> getProvinces();

    @GET("p/{code}?depth=2")
    Single<Province> getDistricts(@Path("code") int provinceCode);
}

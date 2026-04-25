package com.btl_ptit.hotelbooking.data.paging_services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.data.remote.api_services.HotelRestService;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchHotelByNumPersonPagingSource extends RxPagingSource<Integer, MyHotel> {
    @NonNull
    private final HotelRestService mHotelRestService;

    // Các tham số tìm kiếm
    private final String province;
    private final String district;
    private final String checkin;
    private final String checkout;
    private final int numRoom;
    private final int numAdult;
    private final int numChildren;

    public SearchHotelByNumPersonPagingSource(
            @NonNull HotelRestService service,
            String province, String district, String checkin, String checkout,
            int numRoom, int numAdult, int numChildren) {
        this.mHotelRestService = service;
        this.province = province;
        this.district = district;
        this.checkin = checkin;
        this.checkout = checkout;
        this.numRoom = numRoom;
        this.numAdult = numAdult;
        this.numChildren = numChildren;
    }

    @NotNull
    @Override
    public Single<PagingSource.LoadResult<Integer, MyHotel>> loadSingle(@NotNull PagingSource.LoadParams<Integer> params) {
        int pageNumber = params.getKey() != null ? params.getKey() : 1;
        int pageSize = params.getLoadSize();

        // Gọi hàm search_hotels đã tạo ở SQL
        return mHotelRestService.searchHotelsByNumPerson(
                        province, district, checkin, checkout,
                        numRoom, numAdult, numChildren,
                        pageNumber, pageSize)
                .subscribeOn(Schedulers.io())
                .map(list -> toLoadResult(list, pageNumber))
                .onErrorReturn(PagingSource.LoadResult.Error::new);
    }

    private PagingSource.LoadResult<Integer, MyHotel> toLoadResult(@NonNull List<MyHotel> data, int page) {
        return new PagingSource.LoadResult.Page<>(
                data,
                page == 1 ? null : page - 1,
                data.isEmpty() ? null : page + 1
        );
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NotNull PagingState<Integer, MyHotel> state) {
        return null; // Đơn giản hóa cho việc search, refresh sẽ về trang 1
    }
}

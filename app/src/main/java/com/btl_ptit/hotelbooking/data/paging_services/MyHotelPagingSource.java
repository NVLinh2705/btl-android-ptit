package com.btl_ptit.hotelbooking.data.paging_services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.data.remote.api_services.HotelRestService;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyHotelPagingSource extends RxPagingSource<Integer, MyHotel> {
    @NonNull
    private HotelRestService mHotelRestService;

    public MyHotelPagingSource(@NonNull HotelRestService mHotelRestService) {
        this.mHotelRestService = mHotelRestService;
    }

    @NotNull
    @Override
    public Single<LoadResult<Integer, MyHotel>> loadSingle(@NotNull LoadParams<Integer> params) {
        // Start refresh at page 1 if undefined.
        int pageNumber = params.getKey() != null ? params.getKey() : 1;
        int pageSizeLoaded = params.getLoadSize();

        return mHotelRestService.getListHotel(pageNumber, pageSizeLoaded)
                .subscribeOn(Schedulers.io())
                .map(listHotel -> toLoadResult(listHotel, pageNumber))
                .onErrorReturn(LoadResult.Error::new);
    }

    private LoadResult<Integer, MyHotel> toLoadResult(@NonNull List<MyHotel> responseData, int pageNumber) {
        return new LoadResult.Page<>(
                responseData,
                pageNumber == 1 ? null : pageNumber - 1,
                (responseData == null || responseData.isEmpty()) ? null : pageNumber + 1,
                LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NotNull PagingState<Integer, MyHotel> state) {
        // Try to find the page key of the closest page to anchorPosition from
        // either the prevKey or the nextKey; you need to handle nullability
        // here.
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey are null -> anchorPage is the
        //    initial page, so return null.
        Integer anchorPosition = state.getAnchorPosition();
        if (anchorPosition == null) {
            return null;
        }

        LoadResult.Page<Integer, MyHotel> anchorPage = state.closestPageToPosition(anchorPosition);
        if (anchorPage == null) {
            return null;
        }

        Integer prevKey = anchorPage.getPrevKey();
        if (prevKey != null) {
            return prevKey + 1;
        }

        Integer nextKey = anchorPage.getNextKey();
        if (nextKey != null) {
            return nextKey - 1;
        }

        return null;
    }
}

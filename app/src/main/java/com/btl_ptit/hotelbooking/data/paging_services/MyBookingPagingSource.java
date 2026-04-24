package com.btl_ptit.hotelbooking.data.paging_services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.btl_ptit.hotelbooking.data.model.MyBooking;
import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.data.model.MyPopularDestination;
import com.btl_ptit.hotelbooking.data.remote.api_services.BookingRestService;
import com.btl_ptit.hotelbooking.data.session.SessionManager;

import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.core.Single;

public class MyBookingPagingSource extends RxPagingSource<Integer, MyBooking> {

    @NonNull
    private BookingRestService mBookingRestService;

    private  final String status;

    private final String order;

    public MyBookingPagingSource(@NonNull BookingRestService mBookingRestService,String status,String order) {
        this.mBookingRestService = mBookingRestService;
        this.status=status;
        this.order=order;
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, MyBooking>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        // Start refresh at page 1 if undefined.
        int pageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;
        int pageSize = loadParams.getLoadSize();

        //  convert page → offset
        int offset = (pageNumber - 1) * pageSize;
        // status query format cho Supabase: "eq.PENDING" hoặc null
        String statusQuery = (status == null || status.isEmpty()) ? null : "eq." + status;

        // Sắp xếp theo ngày mới nhất
        String orderQuery = "created_at.desc";
        String customerId=SessionManager.getInstance().getUser().getId() !=null ? SessionManager.getInstance().getUser().getId() : "";
        Log.d("DEBUG","customer id= "+customerId);

        return mBookingRestService.getListBooking("*, hotels(*)","eq." + customerId,statusQuery,orderQuery,pageSize, offset)
                .map(listBooking -> toLoadResult(listBooking, pageNumber))
                .onErrorReturn(LoadResult.Error::new);
    }

    private LoadResult<Integer,MyBooking> toLoadResult(@NonNull List<MyBooking> responseData, int pageNumber) {
        return new LoadResult.Page<>(
                responseData,
                pageNumber == 1 ? null : pageNumber - 1,
                (responseData == null || responseData.isEmpty()) ? null : pageNumber + 1,
                LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, MyBooking> pagingState) {
        // Try to find the page key of the closest page to anchorPosition from
        // either the prevKey or the nextKey; you need to handle nullability
        // here.
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey are null -> anchorPage is the
        //    initial page, so return null.
        Integer anchorPosition = pagingState.getAnchorPosition();
        if (anchorPosition == null) {
            return null;
        }

        LoadResult.Page<Integer, MyBooking> anchorPage = pagingState.closestPageToPosition(anchorPosition);
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

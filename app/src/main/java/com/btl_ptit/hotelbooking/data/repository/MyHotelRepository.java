package com.btl_ptit.hotelbooking.data.repository;

import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.data.paging_services.MyHotelPagingSource;
import com.btl_ptit.hotelbooking.data.remote.api_services.HotelRestService;
import com.btl_ptit.hotelbooking.utils.Constants;

import io.reactivex.rxjava3.core.Flowable;

public class MyHotelRepository {

    private final HotelRestService hotelRestService;

    public MyHotelRepository(HotelRestService hotelRestService) {
        this.hotelRestService = hotelRestService;
    }

    public Flowable<PagingData<MyHotel>> getHotelsPaging() {

        // Create new pager
        Pager<Integer, MyHotel> pager = new Pager<>(
                // Create new paging config
                new PagingConfig(
                        Constants.PAGE_SIZE,     // pageSize - Count of items in one page
                        Constants.PREFETCH_DISTANCE,        // prefetchDistance - Number of items to prefetch
                        Constants.ENABLE_PLACEHOLDERS,   // enablePlaceholders - Enable placeholders for data which is not yet loaded
                        Constants.INITIAL_LOAD_SIZE,         // initialLoadSize - Count of items to be loaded initially
                        Constants.MAX_SIZE),      // maxSize - Count of total items to be shown in recyclerview
                () -> new MyHotelPagingSource(hotelRestService, false));      // set paging source

        return PagingRx.getFlowable(pager);
    }

    public Flowable<PagingData<MyHotel>> getHotelsPagingWithFixedQuantity() {

        // Create new pager
        Pager<Integer, MyHotel> pager = new Pager<>(
                // Create new paging config
                new PagingConfig(
                        Constants.NUM_OF_PLACE_HOLDER,     // pageSize - Count of items in one page
                        1,        // prefetchDistance - Number of items to prefetch
                        Constants.ENABLE_PLACEHOLDERS,   // enablePlaceholders - Enable placeholders for data which is not yet loaded
                        Constants.NUM_OF_PLACE_HOLDER,         // initialLoadSize - Count of items to be loaded initially
                        20),      // maxSize - Count of total items to be shown in recyclerview
                () -> new MyHotelPagingSource(hotelRestService, true));      // set paging source

        return PagingRx.getFlowable(pager);
    }
}

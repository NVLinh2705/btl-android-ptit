package com.btl_ptit.hotelbooking.data.repository;

import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.btl_ptit.hotelbooking.data.model.MyBooking;
import com.btl_ptit.hotelbooking.data.paging_services.MyBookingPagingSource;
import com.btl_ptit.hotelbooking.data.remote.api_services.BookingRestService;
import com.btl_ptit.hotelbooking.utils.Constants;

import io.reactivex.rxjava3.core.Flowable;

public class MyBookingRepository {

    private final BookingRestService bookingRestService;

    public MyBookingRepository(BookingRestService bookingRestService) {
        this.bookingRestService = bookingRestService;
    }

    public Flowable<PagingData<MyBooking>> getBookingsPaging() {
        // Create new pager
        Pager<Integer,MyBooking> pager = new Pager<>(
                // Create new paging config
                new PagingConfig(
                        Constants.PAGE_SIZE,     // pageSize - Count of items in one page
                        Constants.PREFETCH_DISTANCE,        // prefetchDistance - Number of items to prefetch
                        Constants.ENABLE_PLACEHOLDERS,   // enablePlaceholders - Enable placeholders for data which is not yet loaded
                        Constants.INITIAL_LOAD_SIZE,         // initialLoadSize - Count of items to be loaded initially
                        Constants.MAX_SIZE),      // maxSize - Count of total items to be shown in recyclerview
                () -> new MyBookingPagingSource(bookingRestService));      // set paging source

        return PagingRx.getFlowable(pager);
    }


}

package com.btl_ptit.hotelbooking.data.repository;

import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.btl_ptit.hotelbooking.data.model.MyBooking;
import com.btl_ptit.hotelbooking.data.model.Review;
import com.btl_ptit.hotelbooking.data.paging_services.MyBookingPagingSource;
import com.btl_ptit.hotelbooking.data.paging_services.ReviewPagingSource;
import com.btl_ptit.hotelbooking.data.remote.api_services.ReviewRestService;
import com.btl_ptit.hotelbooking.utils.Constants;

import io.reactivex.rxjava3.core.Flowable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReviewRepository {

    private final ReviewRestService reviewRestService;

    public Flowable<PagingData<Review>> getReviewsPaging(String hotelId) {
        Pager<Integer, Review> pager = new Pager<>(
                // Create new paging config
                new PagingConfig(
                        Constants.PAGE_SIZE,     // pageSize - Count of items in one page
                        Constants.PREFETCH_DISTANCE,        // prefetchDistance - Number of items to prefetch
                        Constants.ENABLE_PLACEHOLDERS,   // enablePlaceholders - Enable placeholders for data which is not yet loaded
                        Constants.INITIAL_LOAD_SIZE,         // initialLoadSize - Count of items to be loaded initially
                        Constants.MAX_SIZE),      // maxSize - Count of total items to be shown in recyclerview
                () -> new ReviewPagingSource(reviewRestService, hotelId));      // set paging source

        return PagingRx.getFlowable(pager);
    }
}

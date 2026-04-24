package com.btl_ptit.hotelbooking.data.paging_services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.btl_ptit.hotelbooking.data.model.Review;
import com.btl_ptit.hotelbooking.data.remote.api_services.ReviewRestService;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReviewPagingSource extends RxPagingSource<Integer, Review> {

    @NonNull
    private ReviewRestService mReviewRestService;

    @NonNull
    private String hotelId;

    @NonNull
    @Override
    public Single<LoadResult<Integer, Review>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        // Start refresh at page 1 if undefined.
        int pageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;
        int pageSize = loadParams.getLoadSize();

        //  convert page → offset
        int offset = (pageNumber - 1) * pageSize;
        return mReviewRestService.getAllReviews(
                "*,user:users(full_name,avatar_url)",
                "eq." + hotelId,
                "created_at.desc",
                pageSize,
                offset
        ).map(listReview -> toLoadResult(listReview, pageNumber))
                .onErrorReturn(LoadResult.Error::new);

    }

    private LoadResult<Integer, Review> toLoadResult(@NonNull List<Review> responseData, int pageNumber) {
        return new LoadResult.Page<>(
                responseData,
                pageNumber == 1 ? null : pageNumber - 1,
                (responseData == null || responseData.isEmpty()) ? null : pageNumber + 1,
                LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Review> pagingState) {
        return 0;
    }
}

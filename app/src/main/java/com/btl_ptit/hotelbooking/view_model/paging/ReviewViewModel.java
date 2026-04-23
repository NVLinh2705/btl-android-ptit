package com.btl_ptit.hotelbooking.view_model.paging;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagingData;

import com.btl_ptit.hotelbooking.data.model.Review;
import com.btl_ptit.hotelbooking.data.repository.ReviewRepository;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class ReviewViewModel extends ViewModel {

    public Flowable<PagingData<Review>> pagingDataFlow;
    private ReviewRepository repository;
    private String hotelId;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> success = new MutableLiveData<>(false);
    private MutableLiveData<String> error = new MutableLiveData<>(null);

    public ReviewViewModel(ReviewRepository repository, String hotelId) {
        this.repository = repository;
        this.hotelId= hotelId;
        initPaging();
    }

    private void initPaging() {
        Flowable<PagingData<Review>> flowable= repository.getReviewsPaging(hotelId);
        pagingDataFlow = flowable;
    }


    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<Boolean> getSuccess() { return success; }
    public LiveData<String> getError() { return error; }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}

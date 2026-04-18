package com.btl_ptit.hotelbooking.view_model.paging;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;

import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.data.repository.MyHotelRepository;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import kotlinx.coroutines.CoroutineScope;

public class HotelViewModel extends ViewModel {

    public Flowable<PagingData<MyHotel>> pagingDataFlow;
    private MyHotelRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> success = new MutableLiveData<>(false);
    private MutableLiveData<String> error = new MutableLiveData<>(null);

    public HotelViewModel(MyHotelRepository repository, Boolean isRecommended) {
        this.repository = repository;
        if (isRecommended) {
            initPagingWithPagingConfig();
        } else {
            initPaging();
        }
    }

    private void initPagingWithPagingConfig() {
        Flowable<PagingData<MyHotel>> flowable = repository.getHotelsPagingWithFixedQuantity();

        CoroutineScope scope = ViewModelKt.getViewModelScope(this);
        pagingDataFlow = PagingRx.cachedIn(flowable, scope);
    }

    private void initPaging() {
        Flowable<PagingData<MyHotel>> flowable = repository.getHotelsPaging();

        CoroutineScope scope = ViewModelKt.getViewModelScope(this);
        pagingDataFlow = PagingRx.cachedIn(flowable, scope);
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

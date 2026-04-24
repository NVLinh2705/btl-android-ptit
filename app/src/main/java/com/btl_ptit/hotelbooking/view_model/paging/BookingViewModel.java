package com.btl_ptit.hotelbooking.view_model.paging;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.btl_ptit.hotelbooking.data.model.MyBooking;
import com.btl_ptit.hotelbooking.data.repository.MyBookingRepository;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.processors.BehaviorProcessor;
import kotlinx.coroutines.CoroutineScope;

public class BookingViewModel extends ViewModel {
    public Flowable<PagingData<MyBooking>> pagingDataFlow;

    private final BehaviorProcessor<String> statusProcessor = BehaviorProcessor.createDefault("");
    private MyBookingRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> success = new MutableLiveData<>(false);
    private MutableLiveData<String> error = new MutableLiveData<>(null);

    public BookingViewModel(MyBookingRepository repository) {
        this.repository = repository;
        initPaging();
    }

    private void initPaging() {
        CoroutineScope scope = ViewModelKt.getViewModelScope(this);
        Flowable<PagingData<MyBooking>> flowable = statusProcessor
                .flatMap(status -> repository.getBookingsPaging(status, "created_at.desc"));


        pagingDataFlow = PagingRx.cachedIn(flowable, scope);
    }

    public void filterByStatus(String status) {
        statusProcessor.onNext(status);
    }


    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<Boolean> getSuccess() { return success; }
    public LiveData<String> getError() { return error; }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

}

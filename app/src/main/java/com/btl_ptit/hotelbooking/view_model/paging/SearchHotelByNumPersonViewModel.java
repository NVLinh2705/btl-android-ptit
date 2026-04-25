package com.btl_ptit.hotelbooking.view_model.paging;

import androidx.lifecycle.ViewModel;
import androidx.paging.PagingData;

import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.data.repository.MyHotelRepository;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class SearchHotelByNumPersonViewModel extends ViewModel {
    public Flowable<PagingData<MyHotel>> pagingDataFlow;
    private MyHotelRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();


    public SearchHotelByNumPersonViewModel(MyHotelRepository repository) {
        this.repository = repository;
    }


}

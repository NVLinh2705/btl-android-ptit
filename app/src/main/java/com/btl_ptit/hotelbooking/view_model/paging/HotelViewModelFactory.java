package com.btl_ptit.hotelbooking.view_model.paging;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.btl_ptit.hotelbooking.data.repository.MyHotelRepository;

public class HotelViewModelFactory implements ViewModelProvider.Factory {

    private final MyHotelRepository repository;
    private final Boolean isRecommended;

    public HotelViewModelFactory(MyHotelRepository repository, Boolean isRecommended) {
        this.repository = repository;
        this.isRecommended = isRecommended;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HotelViewModel.class)) {
            return (T) new HotelViewModel(repository, isRecommended);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

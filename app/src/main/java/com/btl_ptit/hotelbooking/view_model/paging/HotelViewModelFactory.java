package com.btl_ptit.hotelbooking.view_model.paging;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.btl_ptit.hotelbooking.data.repository.MyHotelRepository;

public class HotelViewModelFactory implements ViewModelProvider.Factory {

    private final MyHotelRepository repository;

    public HotelViewModelFactory(MyHotelRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HotelViewModel.class)) {
            return (T) new HotelViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

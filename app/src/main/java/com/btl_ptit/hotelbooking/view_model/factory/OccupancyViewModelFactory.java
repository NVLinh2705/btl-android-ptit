package com.btl_ptit.hotelbooking.view_model.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.btl_ptit.hotelbooking.data.repository.MyHotelRepository;
import com.btl_ptit.hotelbooking.data.repository.MyMapRepository;
import com.btl_ptit.hotelbooking.view_model.MyMapViewModel;
import com.btl_ptit.hotelbooking.view_model.OccupancyViewModel;

public class OccupancyViewModelFactory implements ViewModelProvider.Factory {
    //    private final MyHotelRepository repository;
    private final MyHotelRepository repository;

    public OccupancyViewModelFactory(MyHotelRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OccupancyViewModel.class)) {
            return (T) new OccupancyViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

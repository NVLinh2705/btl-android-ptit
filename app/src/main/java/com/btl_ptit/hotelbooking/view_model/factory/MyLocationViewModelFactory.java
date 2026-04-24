package com.btl_ptit.hotelbooking.view_model.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.btl_ptit.hotelbooking.data.repository.MyLocationRepository;
import com.btl_ptit.hotelbooking.view_model.MyLocationViewModel;


public class MyLocationViewModelFactory implements ViewModelProvider.Factory {
    private final MyLocationRepository repository;

    public MyLocationViewModelFactory(MyLocationRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MyLocationViewModel.class)) {
            return (T) new MyLocationViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

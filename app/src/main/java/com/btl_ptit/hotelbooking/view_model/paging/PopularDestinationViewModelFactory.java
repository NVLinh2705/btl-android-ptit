package com.btl_ptit.hotelbooking.view_model.paging;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.btl_ptit.hotelbooking.data.repository.MyDestinationRepository;

public class PopularDestinationViewModelFactory implements ViewModelProvider.Factory {
    private final MyDestinationRepository repository;

    public PopularDestinationViewModelFactory(MyDestinationRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PopularDestinationViewModel.class)) {
            return (T) new PopularDestinationViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

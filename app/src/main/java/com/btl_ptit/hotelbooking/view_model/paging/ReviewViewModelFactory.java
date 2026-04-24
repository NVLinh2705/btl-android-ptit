package com.btl_ptit.hotelbooking.view_model.paging;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.btl_ptit.hotelbooking.data.repository.ReviewRepository;

public class ReviewViewModelFactory implements ViewModelProvider.Factory{
    private final ReviewRepository repository;
    private final String hotelId;

    public ReviewViewModelFactory(ReviewRepository repository, String hotelId) {
        this.repository = repository;
        this.hotelId = hotelId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ReviewViewModel.class)) {
            return (T) new ReviewViewModel(repository, hotelId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

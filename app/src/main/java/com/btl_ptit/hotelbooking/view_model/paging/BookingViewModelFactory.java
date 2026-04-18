package com.btl_ptit.hotelbooking.view_model.paging;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.btl_ptit.hotelbooking.data.repository.MyBookingRepository;

public class BookingViewModelFactory implements ViewModelProvider.Factory {
    private final MyBookingRepository repository;
    public BookingViewModelFactory(MyBookingRepository repository) {
        this.repository = repository;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BookingViewModel.class)) {
            return (T) new BookingViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}

package com.btl_ptit.hotelbooking.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.btl_ptit.hotelbooking.data.model.FavoriteResponse;
import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.data.repository.FavoriteRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavoriteViewModel extends ViewModel {
    private final FavoriteRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<List<MyHotel>> favoriteHotels = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public FavoriteViewModel() {
        this.repository = new FavoriteRepository();
    }

    public LiveData<List<MyHotel>> getFavoriteHotels() {
        return favoriteHotels;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadFavorites(String userId) {
        isLoading.setValue(true);
        disposables.add(repository.getFavoriteHotels(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hotels -> {
                    isLoading.setValue(false);
                    favoriteHotels.setValue(hotels);
                }, throwable -> {
                    isLoading.setValue(false);
                    error.setValue(throwable.getMessage());
                }));
    }

    public void toggleLike(String userId, Integer hotelId) {
        disposables.add(repository.toggleLikeHotel(userId, hotelId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unused -> {
                    // Success, maybe reload favorites if in FavoriteFragment
                }, throwable -> {
                    error.setValue(throwable.getMessage());
                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}

package com.btl_ptit.hotelbooking.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.btl_ptit.hotelbooking.data.dto.Province;
import com.btl_ptit.hotelbooking.data.repository.MyLocationRepository;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyLocationViewModel extends ViewModel {
    private final MyLocationRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<List<Province>> provinces = new MutableLiveData<>();
    private final MutableLiveData<Province> provinceDetail = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public MyLocationViewModel(MyLocationRepository repository) {
        this.repository = repository;
    }

    public void loadProvinces() {
        disposables.add(
            repository.fetchAllProvinces()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                provinces::setValue,
                throwable -> error.setValue(throwable.getMessage())
            ));
    }

    public void loadDistricts(int code) {
        disposables.add(
            repository.fetchProvinceDetail(code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                provinceDetail::setValue,
                throwable -> error.setValue(throwable.getMessage())
            ));
    }

    public LiveData<List<Province>> getProvinces() { return provinces; }
    public LiveData<Province> getProvinceDetail() { return provinceDetail; }

    public void clearProvinceDetail() {
        provinceDetail.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}

package com.btl_ptit.hotelbooking.view_model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.btl_ptit.hotelbooking.data.dto.HotelInBoundResponse;
import com.btl_ptit.hotelbooking.data.dto.MapInBoundsParams;
import com.btl_ptit.hotelbooking.data.repository.MyHotelRepository;
import com.btl_ptit.hotelbooking.data.repository.MyMapRepository;
import com.btl_ptit.hotelbooking.utils.Constants;
import com.btl_ptit.hotelbooking.utils.MyUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class MyMapViewModel extends ViewModel {
    private final String TAG = "MyMapViewModel";
//    private final MyHotelRepository mMyHotelRepository;
    private final MyMapRepository mMyMapRepository;
    private final PublishSubject<MapInBoundsParams> mapSubject = PublishSubject.create();
    private final MutableLiveData<List<HotelInBoundResponse>> hotelList = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

//    public MyMapViewModel(MyHotelRepository mMyHotelRepository) {
//        this.mMyHotelRepository = mMyHotelRepository;
//        initSearchHotelInBound();
//    }

    public MyMapViewModel(MyMapRepository mMyMapRepository) {
        this.mMyMapRepository = mMyMapRepository;
        initSearchHotelInBound();
    }

    private void initSearchHotelInBound() {
        disposables.add(
            mapSubject
                .debounce(Constants.TIME_OUT, TimeUnit.MILLISECONDS) // Chờ 0.5s sau khi lướt xong mới gọi API
                .filter(params -> params.getBounds() != null && params.getBounds().getCenter().latitude != 0) // Chặn dữ liệu rác
                // Chỉ gọi API nếu vùng nhìn thay đổi đáng kể
                .distinctUntilChanged((oldParams, newParams) -> {
                    boolean b = MyUtils.isMapChangeInsignificant(
                            oldParams.getBounds(), oldParams.getZoom(),
                            newParams.getBounds(), newParams.getZoom()
                    );
                    if (b) {
                        Log.d(TAG, "Map not changed");
                    }
                    else {
                        Log.d(TAG, "Map changed");
                    }
                    return b;
                }
                )
                .doOnNext(params -> isLoading.postValue(true))
                .switchMapSingle(params -> mMyMapRepository.fetchHotelsInBounds(params.getBounds(), params.getZoom(), params.getPage(), params.getLimit())) // Nếu có yêu cầu mới, hủy yêu cầu cũ
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    hotelList::setValue,
                        throwable -> {
                            Log.e(TAG, "Error: " + throwable.getMessage());
                            isLoading.setValue(false);
                        }
                )
        );
    }
    public void onMapChanged(MapInBoundsParams params) {
        isLoading.setValue(false);
        mapSubject.onNext(params);
    }
    public LiveData<List<HotelInBoundResponse>> getHotels() {
        return hotelList;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}

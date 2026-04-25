package com.btl_ptit.hotelbooking.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.data.repository.MyHotelRepository;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import kotlinx.coroutines.CoroutineScope;

public class OccupancyViewModel extends ViewModel {
    private final MutableLiveData<Integer> persons = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> rooms = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> doubleBed = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> singleBed = new MutableLiveData<>(1);
    private final MutableLiveData<String> selectedLocation = new MutableLiveData<>("");
    private final MutableLiveData<String> checkInDate = new MutableLiveData<>("");
    private final MutableLiveData<String> checkOutDate = new MutableLiveData<>("");
    private final MutableLiveData<Integer> selectedProvinceCode = new MutableLiveData<>(-1);
    private final MutableLiveData<String> selectedProvinceName = new MutableLiveData<>("");
    private final MutableLiveData<String> selectedDistrictName = new MutableLiveData<>("");
    public Flowable<PagingData<MyHotel>> pagingDataFlow;
    private MyHotelRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    public LiveData<String> getSelectedLocation() { return selectedLocation; }
    public LiveData<Integer> getSelectedProvinceCode() { return selectedProvinceCode; }
    public LiveData<String> getSelectedProvinceName() { return selectedProvinceName; }
    public LiveData<String> getSelectedDistrictName() { return selectedDistrictName; }

    public OccupancyViewModel(MyHotelRepository repository) {
        this.repository = repository;
        initPaging();
    }
    public void initPaging() {
        Flowable<PagingData<MyHotel>> flowable = repository.getHotelsSearchByNumPerson(selectedProvinceName.getValue(), selectedDistrictName.getValue(), "2026-04-28", "2026-04-30", rooms.getValue(), doubleBed.getValue(), singleBed.getValue());

        CoroutineScope scope = ViewModelKt.getViewModelScope(this);
        pagingDataFlow = PagingRx.cachedIn(flowable, scope);
    }
    public void setLocationData(String fullLocation, int pCode, String pName, String dName) {
        selectedLocation.setValue(fullLocation);
        selectedProvinceCode.setValue(pCode);
        selectedProvinceName.setValue(pName);
        selectedDistrictName.setValue(dName);
    }
    public LiveData<Integer> getPersons() { return persons; }
    public LiveData<Integer> getRooms() { return rooms; }
    public LiveData<Integer> getDoubleBed() { return doubleBed; }
    public LiveData<Integer> getSingleBed() { return singleBed; }
    public LiveData<String> getCheckInDate() { return checkInDate; }

    public LiveData<String> getCheckOutDate() { return checkOutDate; }


    public void setPersons(int count) {
        if (count >= 1 && count <= 10) {
            persons.setValue(count);
        }
    }

    public void setRooms(int count) {
        if (count >= 1) {
            rooms.setValue(count);
        }
    }

    public void setDoubleBed(int count) {
        if (count >= 1 && count <= 10) {
            if (count == 0 && singleBed.getValue() != null && singleBed.getValue() == 0) return;
            doubleBed.setValue(count);
        }
    }

    public void setSingleBed(int count) {
        if (count >= 0 && count <= 10) {
            if (count == 0 && doubleBed.getValue() != null && doubleBed.getValue() == 0) return;
            singleBed.setValue(count);
        }
    }

    public void incrementPersons() {
        Integer current = persons.getValue();
        if (current != null && current < 10) setPersons(current + 1);
    }

    public void incrementRooms() {
        Integer current = rooms.getValue();
        if (current != null) setRooms(current + 1);
    }

    public void decrementPersons() {
        Integer current = persons.getValue();
        if (current != null && current > 1) setPersons(current - 1);
    }

    public void decrementRooms() {
        Integer current = rooms.getValue();
        if (current != null && current > 1) setRooms(current - 1);
    }

    public void incrementDoubleBed() {
        Integer current = doubleBed.getValue();
        if (current != null && current < 10) setDoubleBed(current + 1);
    }

    public void decrementDoubleBed() {
        Integer current = doubleBed.getValue();
        if (current != null && current > 0) setDoubleBed(current - 1);
    }

    public void incrementSingleBed() {
        Integer current = singleBed.getValue();
        if (current != null && current < 10) setSingleBed(current + 1);
    }

    public void decrementSingleBed() {
        Integer current = singleBed.getValue();
        if (current != null && current > 0) setSingleBed(current - 1);
    }
    public void setCheckInDate(String date) {
        checkInDate.setValue(date);
    }

    public void setCheckOutDate(String date) {
        checkOutDate.setValue(date);
    }

    public void setProvinceName(String name) {
        selectedProvinceName.setValue(name);
    }

    public void setDistrictName(String name) {
        selectedDistrictName.setValue(name);
    }



    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }

}

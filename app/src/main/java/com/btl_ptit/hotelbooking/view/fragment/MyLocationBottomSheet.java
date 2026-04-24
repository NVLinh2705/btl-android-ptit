package com.btl_ptit.hotelbooking.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.btl_ptit.hotelbooking.data.dto.Ward;
import com.btl_ptit.hotelbooking.data.dto.Province;
import com.btl_ptit.hotelbooking.data.remote.MyLocationServiceClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.MyLocationService;
import com.btl_ptit.hotelbooking.data.repository.MyLocationRepository;
import com.btl_ptit.hotelbooking.databinding.LocationBottomSheetBinding;
import com.btl_ptit.hotelbooking.utils.MyUtils;
import com.btl_ptit.hotelbooking.view_model.MyLocationViewModel;
import com.btl_ptit.hotelbooking.view_model.OccupancyViewModel;
import com.btl_ptit.hotelbooking.view_model.factory.MyLocationViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class MyLocationBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "MyLocationBottomSheet";
    private Context mContext;
    private LocationBottomSheetBinding mLocationBottomSheetBinding;
    private MyLocationViewModel mMyLocationViewModel;
    private MyLocationRepository mMyLocationRepository;
    private MyLocationService mMyLocationService;
    private OccupancyViewModel mOccupancyViewModel;;
    private Province mSelectedProvince;

    private List<Province> mProvinceList = new ArrayList<>();
    private List<Ward> mWardList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyLocationService = MyLocationServiceClient.createService(MyLocationService.class);
        mMyLocationRepository = new MyLocationRepository(mMyLocationService);
        mMyLocationViewModel = new ViewModelProvider(requireActivity(), new MyLocationViewModelFactory(mMyLocationRepository)).get(MyLocationViewModel.class);

        mOccupancyViewModel = new ViewModelProvider(requireActivity()).get(OccupancyViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLocationBottomSheetBinding = LocationBottomSheetBinding.inflate(inflater, container, false);
        mContext = getContext();
        View root = mLocationBottomSheetBinding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupObservers();
        setupListeners();

        restorePreviousSelection();

        // Gọi API lấy danh sách tỉnh ngay khi mở
        mMyLocationViewModel.loadProvinces();
    }

    private void setupObservers() {
        // Observe danh sách Tỉnh/Thành
        mMyLocationViewModel.getProvinces().observe(getViewLifecycleOwner(), provinces -> {
            if (provinces != null) {
                mProvinceList = provinces;
                List<String> names = new ArrayList<>();
                for (Province p : provinces) names.add(p.getName());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_dropdown_item_1line, names);
                mLocationBottomSheetBinding.autoCompleteProvince.setAdapter(adapter);
            }
        });

        // Observe chi tiết Tỉnh (để lấy Quận/Huyện)
        mMyLocationViewModel.getProvinceDetail().observe(getViewLifecycleOwner(), provinceDetail -> {
            if (provinceDetail != null && provinceDetail.getDistricts() != null) {
                mWardList = provinceDetail.getDistricts();
                List<String> names = new ArrayList<>();
                for (Ward d : mWardList) names.add(d.getName());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_dropdown_item_1line, names);
                mLocationBottomSheetBinding.autoCompleteDistrict.setAdapter(adapter);

                // Hiện ô chọn Quận/Huyện khi đã có dữ liệu
                mLocationBottomSheetBinding.layoutDistrict.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupListeners() {
        // Khi người dùng chọn một Tỉnh từ gợi ý
        mLocationBottomSheetBinding.autoCompleteProvince.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);

            // Tìm Province object dựa trên tên đã chọn
            for (Province p : mProvinceList) {
                if (p.getName().equals(selectedName)) {
                    mSelectedProvince = p;
                    break;
                }
            }

            if (mSelectedProvince != null) {
                mLocationBottomSheetBinding.autoCompleteDistrict.setText(""); // Reset text huyện cũ
                mMyLocationViewModel.loadDistricts(mSelectedProvince.getCode());
                mLocationBottomSheetBinding.layoutDistrict.setVisibility(View.VISIBLE);
            }
        });

        // Nút xác nhận
        mLocationBottomSheetBinding.btnConfirm.setOnClickListener(v -> {
            String provinceName = mLocationBottomSheetBinding.autoCompleteProvince.getText().toString();
            String districtName = mLocationBottomSheetBinding.autoCompleteDistrict.getText().toString();
            // Lấy code từ object Province đã lưu khi người dùng chọn item
            int pCode = (mSelectedProvince != null) ? mSelectedProvince.getCode() : -1;


            String finalLocation = provinceName;
            if (!districtName.isEmpty()) {
                finalLocation = districtName + ", " + provinceName;
            }

            if (provinceName == null || provinceName.isEmpty()) {
                pCode = -1;
                mSelectedProvince = null;
                if (mMyLocationViewModel != null) {
                    mMyLocationViewModel.clearProvinceDetail();
                }
                finalLocation = null;
                districtName = null;
                provinceName = null;
            }

            // Lưu vào Shared ViewModel
            mOccupancyViewModel.setLocationData(finalLocation, pCode, provinceName, districtName);

            dismiss();
        });
    }

    private void restorePreviousSelection() {
        // 1. Lấy dữ liệu từ Shared ViewModel
        String pName = mOccupancyViewModel.getSelectedProvinceName().getValue();
        String dName = mOccupancyViewModel.getSelectedDistrictName().getValue();
        Integer pCode = mOccupancyViewModel.getSelectedProvinceCode().getValue();

        // 2. Hiển thị lại Tỉnh/Thành
        if (pName != null && !pName.isEmpty()) {
            mLocationBottomSheetBinding.autoCompleteProvince.setText(pName, false); // false để ko hiện dropdown ngay

            // 3. Nếu có code tỉnh, tự động load lại Quận/Huyện
            if (pCode != null && pCode != -1) {
                mSelectedProvince = new Province();
                mSelectedProvince.setCode(pCode);
                mSelectedProvince.setName(pName);

                mMyLocationViewModel.loadDistricts(pCode);
                mLocationBottomSheetBinding.layoutDistrict.setVisibility(View.VISIBLE);

                if (dName != null && !dName.isEmpty()) {
                    mLocationBottomSheetBinding.autoCompleteDistrict.setText(dName, false);
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        MyUtils.setupBottomSheet(dialog, requireActivity());
    }
}

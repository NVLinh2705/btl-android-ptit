package com.btl_ptit.hotelbooking.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.btl_ptit.hotelbooking.databinding.RoomTypeBottomSheetPopupBinding;
import com.btl_ptit.hotelbooking.view_model.OccupancyViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SearchRoomBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "SearchRoomBottomSheetTAG";
    private Context mContext;
    private RoomTypeBottomSheetPopupBinding mRoomTypeBottomSheetPopupBinding;
    private OccupancyViewModel occupancyViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        occupancyViewModel = new ViewModelProvider(requireActivity()).get(OccupancyViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoomTypeBottomSheetPopupBinding = RoomTypeBottomSheetPopupBinding.inflate(inflater, container, false);
        mContext = getContext();
        View root = mRoomTypeBottomSheetPopupBinding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObservers();

        setupClickListeners();

        mRoomTypeBottomSheetPopupBinding.btnApply.setOnClickListener(v -> dismiss());
    }

    private void setupObservers() {
        occupancyViewModel.getPersons().observe(getViewLifecycleOwner(), count ->
                mRoomTypeBottomSheetPopupBinding.rowPerson.tvCount.setText(String.valueOf(count)));

        occupancyViewModel.getRooms().observe(getViewLifecycleOwner(), count ->
                mRoomTypeBottomSheetPopupBinding.rowRoom.tvCount.setText(String.valueOf(count)));

        occupancyViewModel.getDoubleBed().observe(getViewLifecycleOwner(), count ->
                mRoomTypeBottomSheetPopupBinding.rowDoubleBed.tvCount.setText(String.valueOf(count)));

        occupancyViewModel.getSingleBed().observe(getViewLifecycleOwner(), count ->
                mRoomTypeBottomSheetPopupBinding.rowSingleBed.tvCount.setText(String.valueOf(count)));
    }

    private void setupClickListeners() {
        mRoomTypeBottomSheetPopupBinding.rowPerson.btnPlus.setOnClickListener(v -> occupancyViewModel.incrementPersons());
        mRoomTypeBottomSheetPopupBinding.rowPerson.btnMinus.setOnClickListener(v -> occupancyViewModel.decrementPersons());

        mRoomTypeBottomSheetPopupBinding.rowRoom.btnPlus.setOnClickListener(v -> {occupancyViewModel.incrementRooms();});
        mRoomTypeBottomSheetPopupBinding.rowRoom.btnMinus.setOnClickListener(v -> {occupancyViewModel.decrementRooms();});

        mRoomTypeBottomSheetPopupBinding.rowDoubleBed.btnPlus.setOnClickListener(v -> {occupancyViewModel.incrementDoubleBed();});
        mRoomTypeBottomSheetPopupBinding.rowDoubleBed.btnMinus.setOnClickListener(v -> {occupancyViewModel.decrementDoubleBed();});

        mRoomTypeBottomSheetPopupBinding.rowSingleBed.btnPlus.setOnClickListener(v -> {occupancyViewModel.incrementSingleBed();});
        mRoomTypeBottomSheetPopupBinding.rowSingleBed.btnMinus.setOnClickListener(v -> {occupancyViewModel.decrementSingleBed();});
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                // Dùng WRAP_CONTENT để nó tự co theo nội dung khi nội dung ngắn
                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

                // 2. Set Max Height để nếu nội dung quá dài, nó không vượt quá 90% màn hình
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                behavior.setMaxHeight((int) (screenHeight * 0.90));

                // 3. Ép trạng thái ban đầu là EXPANDED
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                // 4. Bỏ qua trạng thái lửng lơ (Collapsed), vuốt xuống là đóng luôn
                behavior.setSkipCollapsed(true);
            }
        }
    }
}

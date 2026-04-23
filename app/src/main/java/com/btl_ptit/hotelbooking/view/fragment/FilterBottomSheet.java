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

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.databinding.LayoutFilterBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    private static final String TAG = "FilterBottomSheetTAG";
    private LayoutFilterBottomSheetBinding mLayoutFilterBottomSheetBinding;
    private Context mContext;
    private String[] guests;
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo mảng dữ liệu
        guests = getResources().getStringArray(R.array.guest_options);

        // Tạo Adapter (Sử dụng layout mặc định của Android hoặc custom)
        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                guests
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayoutFilterBottomSheetBinding = LayoutFilterBottomSheetBinding.inflate(inflater, container, false);
        mContext = getContext();
        View root = mLayoutFilterBottomSheetBinding.getRoot();


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLayoutFilterBottomSheetBinding.priceSlider.setValueFrom(0f);
        mLayoutFilterBottomSheetBinding.priceSlider.setValueTo(100f);
        mLayoutFilterBottomSheetBinding.priceSlider.setValues(0f, 100f);

        mLayoutFilterBottomSheetBinding.autoCompletePlaceholder.setAdapter(adapter);
        mLayoutFilterBottomSheetBinding.autoCompletePlaceholder.setText(guests[0], false);
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

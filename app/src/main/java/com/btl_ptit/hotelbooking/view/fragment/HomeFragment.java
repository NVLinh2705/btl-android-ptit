package com.btl_ptit.hotelbooking.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.btl_ptit.hotelbooking.databinding.FragmentHomeBinding;
import com.btl_ptit.hotelbooking.view.activity.HotelInfoActivity;
import com.google.android.material.appbar.AppBarLayout;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding mFragmentHomeBinding;
    private Context mContext;
    private String TAG = "HomeFragmentTAG";

    private Button btn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        View view = mFragmentHomeBinding.getRoot();
        mContext = getContext();

        mFragmentHomeBinding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBar, int verticalOffset) {
                int totalRange = appBar.getTotalScrollRange();
                if (totalRange == 0) return;

                // Tính toán tỷ lệ phần trăm cuộn (0.0 -> 1.0)
                float percentage = (float) Math.abs(verticalOffset) / totalRange;

                mFragmentHomeBinding.headerContainer.setPivotX(mFragmentHomeBinding.headerContainer.getWidth() / 2f);
                mFragmentHomeBinding.headerContainer.setPivotY(mFragmentHomeBinding.headerContainer.getHeight() / 2f);

                // Thu nhỏ từ 100% xuống còn 70% (giảm 30% theo nhịp cuộn)
                float scaleValue = 1.0f - (percentage * 0.4f);
                mFragmentHomeBinding.headerContainer.setScaleX(scaleValue);
                mFragmentHomeBinding.headerContainer.setScaleY(scaleValue);

                // 3. HIỆU ỨNG MỜ DẦN
                mFragmentHomeBinding.headerContainer.setAlpha(1.0f - percentage);

                // 4. CHỐNG TRÔI NGƯỢC
                mFragmentHomeBinding.headerContainer.setTranslationY(Math.abs(verticalOffset) * 0.6f);
            }
        });

        mFragmentHomeBinding.btn.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, HotelInfoActivity.class);
            startActivity(intent);
        });
        return view;
    }
}
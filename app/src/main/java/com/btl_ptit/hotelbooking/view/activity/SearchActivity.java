package com.btl_ptit.hotelbooking.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.databinding.ActivitySearchBinding;
import com.btl_ptit.hotelbooking.view.fragment.FilterBottomSheet;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivityTAG";
    private ActivitySearchBinding mActivitySearchBinding;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySearchBinding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(mActivitySearchBinding.getRoot());

        mContext = this;
        initListeners();
    }

    private void initListeners() {
        mActivitySearchBinding.btnBack.setOnClickListener(view -> {
            finish();
        });
        mActivitySearchBinding.btnInnerFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterBottomSheet filterSheet = new FilterBottomSheet();
                filterSheet.show(getSupportFragmentManager(), "Filter");
            }
        });
    }
}
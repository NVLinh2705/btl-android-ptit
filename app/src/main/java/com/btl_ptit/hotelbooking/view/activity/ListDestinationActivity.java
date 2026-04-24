package com.btl_ptit.hotelbooking.view.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.databinding.ActivityListDestinationBinding;
import com.google.android.material.appbar.AppBarLayout;

public class ListDestinationActivity extends AppCompatActivity {

    private static final String TAG = "ListDestinationActivityTAG";
    private ActivityListDestinationBinding mActivityListDestinationBinding;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityListDestinationBinding = ActivityListDestinationBinding.inflate(getLayoutInflater());
        setContentView(mActivityListDestinationBinding.getRoot());

        mContext = this;

        initActionBar();
    }

    private void initActionBar() {
        // Gán toolbar thành action bar
        setSupportActionBar(mActivityListDestinationBinding.toolbar);

        // toolbar
//        Typeface typeface = ResourcesCompat.getFont(this, R.font.plus_jakarta);
//        mActivityListDestinationBinding.collapsingToolbar.setCollapsedTitleTypeface(typeface);
//        mActivityListDestinationBinding.collapsingToolbar.setExpandedTitleTypeface(typeface);
//        mActivityListDestinationBinding.collapsingToolbar.setTitle(getString(R.string.trending_destinations2));

        // Hiển thị nút back
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
//
//        mActivityListDestinationBinding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean isToolbarVisible = false;
//            int scrollRange = -1;
//
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//
//                // Khi collapse hoàn toàn
//                if (scrollRange + verticalOffset == 0) {
//                    if (!isToolbarVisible) {
//                        int color = ContextCompat.getColor(mContext, R.color.md_theme_primary);
//                        mActivityListDestinationBinding.toolbar.setBackgroundColor(color);
//                        isToolbarVisible = true;
//                    }
//                } else {
//                    if (isToolbarVisible) {
//                        mActivityListDestinationBinding.toolbar.setBackgroundColor(Color.TRANSPARENT);
//                        isToolbarVisible = false;
//                    }
//                }
//            }
//        });
    }
}
package com.btl_ptit.hotelbooking.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.btl_ptit.hotelbooking.databinding.ActivityHotelDetailBinding;
import com.btl_ptit.hotelbooking.view.adapter.HotelDetailTabsPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.LocalDate;

public class HotelDetailActivity extends AppCompatActivity {

    public static final String EXTRA_HOTEL_ID = "hotel_id";
    public static final String EXTRA_HOTEL_NAME = "hotel_name";
    public static final String EXTRA_DESCRIPTION = "hotel_description";
    public static final String EXTRA_POLICIES_JSON = "hotel_policies_json";
    public static final String EXTRA_INITIAL_TAB = "initial_tab";

    public static final int TAB_DESCRIPTION = 0;
    public static final int TAB_FACILITIES = 1;
    public static final int TAB_POLICIES = 2;
    public static final int TAB_REVIEWS = 3;

    private static final String[] TAB_TITLES = new String[]{"Mô tả", "Tiện nghi", "Chính sách", "Đánh giá"};

    private ActivityHotelDetailBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityHotelDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setupToolbar();
        setupTabs();
        setupBottomCta();
    }

    private void setupToolbar() {
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra(EXTRA_HOTEL_NAME));
        }
        b.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTabs() {
        HotelDetailTabsPagerAdapter pagerAdapter = new HotelDetailTabsPagerAdapter(this, getIntent().getExtras());
        b.viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(b.tabLayout, b.viewPager, (tab, position) -> tab.setText(TAB_TITLES[position])).attach();

        int initialTab = getIntent().getIntExtra(EXTRA_INITIAL_TAB, TAB_DESCRIPTION);
        b.viewPager.post(() -> b.viewPager.setCurrentItem(Math.max(0, Math.min(initialTab, TAB_TITLES.length - 1)), false));
    }

    private void setupBottomCta() {
        b.btnSelectRoom.setOnClickListener(v -> {
            int hotelId = getIntent().getIntExtra(EXTRA_HOTEL_ID, -1);
            Intent intent = new Intent(this, ListRoomTypeActivity.class);
            intent.putExtra(ListRoomTypeActivity.EXTRA_HOTEL_ID, hotelId);

            LocalDate today = LocalDate.now();
            String checkinDate = getIntent().getStringExtra(ListRoomTypeActivity.EXTRA_CHECKIN_DATE);
            String checkoutDate = getIntent().getStringExtra(ListRoomTypeActivity.EXTRA_CHECKOUT_DATE);
            if (checkinDate == null || checkinDate.trim().isEmpty()) {
                checkinDate = today.toString();
            }
            if (checkoutDate == null || checkoutDate.trim().isEmpty()) {
                checkoutDate = today.plusDays(1).toString();
            }

            intent.putExtra(ListRoomTypeActivity.EXTRA_CHECKIN_DATE, checkinDate);
            intent.putExtra(ListRoomTypeActivity.EXTRA_CHECKOUT_DATE, checkoutDate);
            intent.putExtra(ListRoomTypeActivity.EXTRA_ROOM_QUANTITY,
                    getIntent().getIntExtra(ListRoomTypeActivity.EXTRA_ROOM_QUANTITY, 1));
            intent.putExtra(ListRoomTypeActivity.EXTRA_ADULTS,
                    getIntent().getIntExtra(ListRoomTypeActivity.EXTRA_ADULTS, 2));
            intent.putExtra(ListRoomTypeActivity.EXTRA_CHILDREN,
                    getIntent().getIntExtra(ListRoomTypeActivity.EXTRA_CHILDREN, 0));

            startActivity(intent);
        });
    }

    @NonNull
    public static Intent createIntent(
            @NonNull AppCompatActivity activity,
            int hotelId,
            String hotelName,
            String description,
            String policiesJson,
            int initialTab
    ) {
        Intent intent = new Intent(activity, HotelDetailActivity.class);
        intent.putExtra(EXTRA_HOTEL_ID, hotelId);
        intent.putExtra(EXTRA_HOTEL_NAME, hotelName);
        intent.putExtra(EXTRA_DESCRIPTION, description);
        intent.putExtra(EXTRA_POLICIES_JSON, policiesJson);
        intent.putExtra(EXTRA_INITIAL_TAB, initialTab);
        return intent;
    }
}



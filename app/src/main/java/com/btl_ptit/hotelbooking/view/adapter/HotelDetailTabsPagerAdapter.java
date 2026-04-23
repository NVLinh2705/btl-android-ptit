package com.btl_ptit.hotelbooking.view.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.btl_ptit.hotelbooking.view.activity.HotelDetailActivity;
import com.btl_ptit.hotelbooking.view.fragment.hoteldetail.DescriptionTabFragment;
import com.btl_ptit.hotelbooking.view.fragment.hoteldetail.FacilitiesTabFragment;
import com.btl_ptit.hotelbooking.view.fragment.hoteldetail.PoliciesTabFragment;
import com.btl_ptit.hotelbooking.view.fragment.hoteldetail.ReviewsTabFragment;

public class HotelDetailTabsPagerAdapter extends FragmentStateAdapter {

    private final Bundle extras;

    public HotelDetailTabsPagerAdapter(@NonNull FragmentActivity fragmentActivity, @Nullable Bundle extras) {
        super(fragmentActivity);
        this.extras = extras == null ? new Bundle() : extras;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int hotelId = extras.getInt(HotelDetailActivity.EXTRA_HOTEL_ID, -1);
        String description = extras.getString(HotelDetailActivity.EXTRA_DESCRIPTION, "");
        String policiesJson = extras.getString(HotelDetailActivity.EXTRA_POLICIES_JSON, "[]");

        if (position == HotelDetailActivity.TAB_FACILITIES) {
            return FacilitiesTabFragment.newInstance(hotelId);
        }
        if (position == HotelDetailActivity.TAB_POLICIES) {
            return PoliciesTabFragment.newInstance(policiesJson);
        }
        if (position == HotelDetailActivity.TAB_REVIEWS) {
            return ReviewsTabFragment.newInstance(hotelId);
        }
        return DescriptionTabFragment.newInstance(description);
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}


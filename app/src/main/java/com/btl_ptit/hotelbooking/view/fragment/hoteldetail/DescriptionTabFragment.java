package com.btl_ptit.hotelbooking.view.fragment.hoteldetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.btl_ptit.hotelbooking.databinding.FragmentHotelDescriptionBinding;

public class DescriptionTabFragment extends Fragment {

    private static final String ARG_DESCRIPTION = "arg_description";

    public static DescriptionTabFragment newInstance(String description) {
        DescriptionTabFragment fragment = new DescriptionTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DESCRIPTION, description);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentHotelDescriptionBinding b = FragmentHotelDescriptionBinding.inflate(inflater, container, false);
        String description = getArguments() != null ? getArguments().getString(ARG_DESCRIPTION, "") : "";
        b.txtDescription.setText(description == null || description.trim().isEmpty() ? "Chưa có mô tả" : description);
        return b.getRoot();
    }
}


package com.btl_ptit.hotelbooking.view.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.databinding.FragmentMyBookingBinding;

public class MyBookingFragment extends Fragment {

    private FragmentMyBookingBinding mFragmentMyBookingBinding;
    private Context mContext;
    private String TAG = "MyBookingFragmentTAG";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentMyBookingBinding = FragmentMyBookingBinding.inflate(inflater, container, false);

        View view = mFragmentMyBookingBinding.getRoot();
        mContext = getContext();
        return view;
    }
}
package com.btl_ptit.hotelbooking.view.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding mFragmentProfileBinding;
    private Context mContext;
    private String TAG = "ProfileFragmentTAG";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false);

        View view = mFragmentProfileBinding.getRoot();
        mContext = getContext();
        return view;
    }
}
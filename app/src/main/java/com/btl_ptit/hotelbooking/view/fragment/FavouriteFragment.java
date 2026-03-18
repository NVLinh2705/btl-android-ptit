package com.btl_ptit.hotelbooking.view.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.databinding.FragmentFavouriteBinding;
import com.btl_ptit.hotelbooking.databinding.FragmentHomeBinding;

public class FavouriteFragment extends Fragment {

    private FragmentFavouriteBinding mFragmentFavouriteBinding;
    private Context mContext;
    private String TAG = "FavouriteFragmentTAG";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentFavouriteBinding = FragmentFavouriteBinding.inflate(inflater, container, false);

        View view = mFragmentFavouriteBinding.getRoot();
        mContext = getContext();
        return view;
    }
}
package com.btl_ptit.hotelbooking.view.fragment.hoteldetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btl_ptit.hotelbooking.data.dto.Policy;
import com.btl_ptit.hotelbooking.databinding.FragmentHotelPoliciesBinding;
import com.btl_ptit.hotelbooking.view.adapter.PolicyAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PoliciesTabFragment extends Fragment {

    private static final String ARG_POLICIES_JSON = "arg_policies_json";

    public static PoliciesTabFragment newInstance(String policiesJson) {
        PoliciesTabFragment fragment = new PoliciesTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POLICIES_JSON, policiesJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentHotelPoliciesBinding b = FragmentHotelPoliciesBinding.inflate(inflater, container, false);

        PolicyAdapter adapter = new PolicyAdapter();
        b.rvPolicies.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.rvPolicies.setAdapter(adapter);

        String policiesJson = getArguments() != null ? getArguments().getString(ARG_POLICIES_JSON, "[]") : "[]";
        Type type = new TypeToken<List<Policy>>() {}.getType();
        List<Policy> policies = new Gson().fromJson(policiesJson, type);
        adapter.submitList(policies == null ? new ArrayList<>() : policies);

        return b.getRoot();
    }
}


package com.btl_ptit.hotelbooking.view.fragment.hoteldetail;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btl_ptit.hotelbooking.data.dto.HotelFacility;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.SupabaseRestService;
import com.btl_ptit.hotelbooking.databinding.FragmentHotelFacilitiesBinding;
import com.btl_ptit.hotelbooking.view.adapter.FacilitiesGroupedAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FacilitiesTabFragment extends Fragment {

    private static final String ARG_HOTEL_ID = "arg_hotel_id";

    public static FacilitiesTabFragment newInstance(int hotelId) {
        FacilitiesTabFragment fragment = new FacilitiesTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_HOTEL_ID, hotelId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentHotelFacilitiesBinding b = FragmentHotelFacilitiesBinding.inflate(inflater, container, false);
        FacilitiesGroupedAdapter adapter = new FacilitiesGroupedAdapter();

        b.rvFacilities.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.rvFacilities.setAdapter(adapter);

        b.edtSearchFacilities.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s != null ? s.toString() : "");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        int hotelId = getArguments() != null ? getArguments().getInt(ARG_HOTEL_ID, -1) : -1;
        if (hotelId <= 0) {
            b.txtState.setVisibility(View.VISIBLE);
            b.txtState.setText("Không có thông tin tiện nghi");
            return b.getRoot();
        }

        SupabaseRestService service = SupabaseClient.createService(SupabaseRestService.class);
        service.getHotelFacilities(
                hotelId
        ).enqueue(new Callback<List<HotelFacility>>() {
            @Override
            public void onResponse(Call<List<HotelFacility>> call, Response<List<HotelFacility>> response) {
                List<HotelFacility> data = response.body();
                adapter.submitList(data);
                b.txtState.setVisibility((data == null || data.isEmpty()) ? View.VISIBLE : View.GONE);
                if (data == null || data.isEmpty()) {
                    b.txtState.setText("Không có thông tin tiện nghi");
                }
            }

            @Override
            public void onFailure(Call<List<HotelFacility>> call, Throwable t) {
                b.txtState.setVisibility(View.VISIBLE);
                b.txtState.setText("Tai tien nghi that bai");
                Toast.makeText(requireContext(), "Tai tien nghi that bai", Toast.LENGTH_SHORT).show();
            }
        });

        return b.getRoot();
    }
}



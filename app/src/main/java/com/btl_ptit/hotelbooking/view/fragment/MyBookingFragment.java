package com.btl_ptit.hotelbooking.view.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.data.remote.MockApiClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.HotelRestService;
import com.btl_ptit.hotelbooking.databinding.FragmentMyBookingBinding;
import com.btl_ptit.hotelbooking.view.activity.LoginActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookingFragment extends Fragment {

    private FragmentMyBookingBinding mFragmentMyBookingBinding;
    private Context mContext;
    private String TAG = "MyBookingFragmentTAG";

    private HotelRestService hotelRestService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentMyBookingBinding = FragmentMyBookingBinding.inflate(inflater, container, false);

        View view = mFragmentMyBookingBinding.getRoot();
        mContext = getContext();

        hotelRestService = MockApiClient.createService(HotelRestService.class);

        mFragmentMyBookingBinding.btnMyBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                hotelRestService.getListHotel(1, 10).enqueue(new Callback<List<MyHotel>>() {
//                    @Override
//                    public void onResponse(Call<List<MyHotel>> call, Response<List<MyHotel>> response) {
//                        if (!response.isSuccessful() || response.body() == null) {
//                            Toast.makeText(getActivity(), "Call MockAPI Failed!!!", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        List<MyHotel> listMyHotel = response.body();
//                        String result = "";
//                        for (MyHotel myHotel : listMyHotel) {
//                            result += myHotel.getName() + " - " + myHotel.getAvatar() + " - " + myHotel.getId() + "\n";
//                        }
//                        Log.d(TAG, "onResponse: " + result);
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<MyHotel>> call, Throwable t) {
//
//                    }
//                });
            }
        });

        return view;
    }
}
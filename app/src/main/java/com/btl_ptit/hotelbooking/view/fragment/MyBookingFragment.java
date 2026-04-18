package com.btl_ptit.hotelbooking.view.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.model.MyBooking;
import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.BookingRestService;
import com.btl_ptit.hotelbooking.data.remote.api_services.HotelRestService;
import com.btl_ptit.hotelbooking.data.repository.MyBookingRepository;
import com.btl_ptit.hotelbooking.databinding.FragmentMyBookingBinding;
import com.btl_ptit.hotelbooking.listener.OnBookingClickListener;
import com.btl_ptit.hotelbooking.utils.paging.MyComparator;
import com.btl_ptit.hotelbooking.view.activity.LoginActivity;
import com.btl_ptit.hotelbooking.view.adapter.BookingAdapter;
import com.btl_ptit.hotelbooking.view.adapter.LoadStateAdapter;
import com.btl_ptit.hotelbooking.view_model.paging.BookingViewModel;
import com.btl_ptit.hotelbooking.view_model.paging.BookingViewModelFactory;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookingFragment extends Fragment {

    private FragmentMyBookingBinding mFragmentMyBookingBinding;
    private Context mContext;
    private String TAG = "MyBookingFragmentTAG";

    private BookingRestService bookingRestService;

    private BookingViewModel bookingViewModel;

    private MyBookingRepository myBookingRepository;
    private BookingAdapter bookingAdapter;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentMyBookingBinding = FragmentMyBookingBinding.inflate(inflater, container, false);

        View view = mFragmentMyBookingBinding.getRoot();
        mContext = getContext();
        bookingRestService = SupabaseClient.createService(BookingRestService.class);
        myBookingRepository = new MyBookingRepository(bookingRestService);
        bookingViewModel = new ViewModelProvider(requireActivity(), new BookingViewModelFactory(myBookingRepository)).get(BookingViewModel.class);
        initBookingAdapter();
        compositeDisposable.clear();
        compositeDisposable.add(
                bookingViewModel.pagingDataFlow
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(pagingData ->{
                            Log.d("DEBUG", "PagingData received");
                            Log.d("DEBUG", "PagingData received: " + pagingData);
                            bookingAdapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData);
                        } )
        );


        return view;
    }

    private void initBookingAdapter() {
        bookingAdapter = new BookingAdapter(new MyComparator<MyBooking>(), requireContext(), new OnBookingClickListener(){
            @Override
            public void onBookingClick(MyBooking myBooking) {
                Toast.makeText(mContext, "Booking clicked", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        mFragmentMyBookingBinding.rvBooking.setLayoutManager(layoutManager);
        mFragmentMyBookingBinding.rvBooking.setHasFixedSize(true);
        mFragmentMyBookingBinding.rvBooking.setAdapter(
                bookingAdapter.withLoadStateFooter(
                        new LoadStateAdapter(v -> bookingAdapter.retry())
                )
        );
        bookingAdapter.addLoadStateListener(loadState -> {
            if (loadState.getRefresh() instanceof androidx.paging.LoadState.NotLoading
                    && bookingAdapter.getItemCount() == 0) {

                Toast.makeText(mContext, "No booking", Toast.LENGTH_SHORT).show();
            }

            return null;
        });


    }
}
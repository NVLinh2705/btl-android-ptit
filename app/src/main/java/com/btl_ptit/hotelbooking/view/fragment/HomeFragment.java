package com.btl_ptit.hotelbooking.view.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.remote.MockApiClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.HotelRestService;
import com.btl_ptit.hotelbooking.data.repository.MyHotelRepository;
import com.btl_ptit.hotelbooking.databinding.FragmentHomeBinding;
import com.btl_ptit.hotelbooking.utils.paging.MyHotelComparator;
import com.btl_ptit.hotelbooking.view.adapter.HotelAdapter;
import com.btl_ptit.hotelbooking.view.adapter.HotelLoadStateAdapter;
import com.btl_ptit.hotelbooking.view_model.paging.HotelViewModel;
import com.btl_ptit.hotelbooking.view_model.paging.HotelViewModelFactory;
import com.google.android.material.appbar.AppBarLayout;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding mFragmentHomeBinding;
    private Context mContext;
    private String TAG = "HomeFragmentTAG";

    private HotelAdapter mHotelAdapter;
    private HotelViewModel mHotelViewModel;
    private HotelRestService mHotelRestService;
    private MyHotelRepository mMyHotelRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        View view = mFragmentHomeBinding.getRoot();
        mContext = getContext();

        mFragmentHomeBinding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBar, int verticalOffset) {
                int totalRange = appBar.getTotalScrollRange();
                if (totalRange == 0) return;

                // Tính toán tỷ lệ phần trăm cuộn (0.0 -> 1.0)
                float percentage = (float) Math.abs(verticalOffset) / totalRange;

                mFragmentHomeBinding.headerContainer.setPivotX(mFragmentHomeBinding.headerContainer.getWidth() / 2f);
                mFragmentHomeBinding.headerContainer.setPivotY(mFragmentHomeBinding.headerContainer.getHeight() / 2f);

                // Thu nhỏ từ 100% xuống còn 70% (giảm 30% theo nhịp cuộn)
                float scaleValue = 1.0f - (percentage * 0.4f);
                mFragmentHomeBinding.headerContainer.setScaleX(scaleValue);
                mFragmentHomeBinding.headerContainer.setScaleY(scaleValue);

                // 3. HIỆU ỨNG MỜ DẦN
                mFragmentHomeBinding.headerContainer.setAlpha(1.0f - percentage);

                // 4. CHỐNG TRÔI NGƯỢC
                mFragmentHomeBinding.headerContainer.setTranslationY(Math.abs(verticalOffset) * 0.6f);
            }
        });

        mHotelRestService = MockApiClient.createService(HotelRestService.class);
        mMyHotelRepository = new MyHotelRepository(mHotelRestService);
        mHotelViewModel = new ViewModelProvider(requireActivity(), new HotelViewModelFactory(mMyHotelRepository)).get(HotelViewModel.class);

        initHotelAdapter();

        compositeDisposable.add(
            mHotelViewModel.pagingDataFlow
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(pagingData -> mHotelAdapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData))
        );



        return view;
    }

    private void initHotelAdapter() {
        mHotelAdapter = new HotelAdapter(new MyHotelComparator(), requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );

        mFragmentHomeBinding.recyclerViewHotels.setLayoutManager(layoutManager);
        mFragmentHomeBinding.recyclerViewHotels.setHasFixedSize(true);

        mFragmentHomeBinding.recyclerViewHotels.setAdapter(
                mHotelAdapter.withLoadStateFooter(
                        new HotelLoadStateAdapter(v -> mHotelAdapter.retry())
                )
        );

        mHotelAdapter.addLoadStateListener(loadState -> {
            if (loadState.getRefresh() instanceof LoadState.Loading || loadState.getRefresh() instanceof LoadState.Error) {
                mFragmentHomeBinding.shimmerLayout.startShimmer();
                mFragmentHomeBinding.shimmerLayout.setVisibility(View.VISIBLE);
                mFragmentHomeBinding.recyclerViewHotels.setVisibility(View.GONE);
            } else {
                mFragmentHomeBinding.shimmerLayout.stopShimmer();
                mFragmentHomeBinding.shimmerLayout.setVisibility(View.GONE);
                mFragmentHomeBinding.recyclerViewHotels.setVisibility(View.VISIBLE);
            }
            return null;
        });
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.clear();
        super.onDestroyView();
    }
}
package com.btl_ptit.hotelbooking.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.btl_ptit.hotelbooking.data.model.MyPopularDestination;
import com.btl_ptit.hotelbooking.data.remote.MockApiClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.MyDestinationRestService;
import com.btl_ptit.hotelbooking.data.repository.MyDestinationRepository;
import com.btl_ptit.hotelbooking.databinding.FragmentHomeBinding;
import com.btl_ptit.hotelbooking.databinding.PopularDestinationItemBinding;
import com.btl_ptit.hotelbooking.listener.OnDestinationClickListener;
import com.btl_ptit.hotelbooking.utils.paging.MyComparator;
import com.btl_ptit.hotelbooking.view.activity.HotelDetailActivity;
import com.btl_ptit.hotelbooking.view.adapter.LoadStateAdapter;
import com.btl_ptit.hotelbooking.view.adapter.PopularDestinationAdapter;
import com.btl_ptit.hotelbooking.view_model.paging.PopularDestinationViewModel;
import com.btl_ptit.hotelbooking.view_model.paging.PopularDestinationViewModelFactory;
import com.google.android.material.appbar.AppBarLayout;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding mFragmentHomeBinding;
    private Context mContext;
    private String TAG = "HomeFragmentTAG";

    private PopularDestinationAdapter mPopularDestinationAdapter;
    private PopularDestinationViewModel mPopularDestinationViewModel;
    private MyDestinationRestService mMyDestinationRestService;
    private MyDestinationRepository mMyDestinationRepository;;
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

        mMyDestinationRestService = MockApiClient.createService(MyDestinationRestService.class);
        mMyDestinationRepository = new MyDestinationRepository(mMyDestinationRestService);
        mPopularDestinationViewModel = new ViewModelProvider(requireActivity(), new PopularDestinationViewModelFactory(mMyDestinationRepository)).get(PopularDestinationViewModel.class);

        initHotelAdapter();

        compositeDisposable.add(
            mPopularDestinationViewModel.pagingDataFlow
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(pagingData -> mPopularDestinationAdapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData))
        );



        return view;
    }

    private void initHotelAdapter() {
        mPopularDestinationAdapter = new PopularDestinationAdapter(new MyComparator<MyPopularDestination>(), requireContext(), new OnDestinationClickListener() {
            @Override
            public void onDestinationClick(MyPopularDestination destination) {
                Intent intent = new Intent(mContext, HotelDetailActivity.class);
                startActivity(intent);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );

        mFragmentHomeBinding.recyclerViewDestinations.setLayoutManager(layoutManager);
        mFragmentHomeBinding.recyclerViewDestinations.setHasFixedSize(true);

        mFragmentHomeBinding.recyclerViewDestinations.setAdapter(
                mPopularDestinationAdapter.withLoadStateFooter(
                        new LoadStateAdapter(v -> mPopularDestinationAdapter.retry())
                )
        );

        mPopularDestinationAdapter.addLoadStateListener(loadState -> {
            if (loadState.getRefresh() instanceof LoadState.Loading || loadState.getRefresh() instanceof LoadState.Error) {
                if (mFragmentHomeBinding.shimmerContainer.getChildCount() == 0) {
                    setupShimmerPlaceholder();
                }
                mFragmentHomeBinding.shimmerContainer.setVisibility(View.VISIBLE);
                mFragmentHomeBinding.recyclerViewDestinations.setVisibility(View.GONE);
            } else {
                mFragmentHomeBinding.shimmerContainer.setVisibility(View.GONE);
                mFragmentHomeBinding.recyclerViewDestinations.setVisibility(View.VISIBLE);
            }
            return null;
        });
    }

    private void setupShimmerPlaceholder() {
        int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;

        float density = mContext.getResources().getDisplayMetrics().density;
        int itemWidthPx = (int) (200 * density);

        int count = (int) Math.ceil((float) screenWidth / itemWidthPx);

        count =  Math.max(1, count + 1);

        mFragmentHomeBinding.shimmerContainer.removeAllViews();
        for (int i = 0; i < count; i++) {
            // Inflate item
            PopularDestinationItemBinding shimmerBinding = PopularDestinationItemBinding.inflate(getLayoutInflater(), mFragmentHomeBinding.shimmerContainer, false);

            if (shimmerBinding.shimmerLayout != null) {
                shimmerBinding.shimmerLayout.startShimmer();
            }

            mFragmentHomeBinding.shimmerContainer.addView(shimmerBinding.getRoot());
        }
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.clear();
        mFragmentHomeBinding = null;
        super.onDestroyView();
    }
}
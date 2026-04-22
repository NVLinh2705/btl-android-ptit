package com.btl_ptit.hotelbooking.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.data.model.MyPopularDestination;
import com.btl_ptit.hotelbooking.data.remote.MockApiClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.HotelRestService;
import com.btl_ptit.hotelbooking.data.remote.api_services.MyDestinationRestService;
import com.btl_ptit.hotelbooking.data.repository.MyDestinationRepository;
import com.btl_ptit.hotelbooking.data.repository.MyHotelRepository;
import com.btl_ptit.hotelbooking.databinding.FragmentHomeBinding;
import com.btl_ptit.hotelbooking.databinding.HotelItemBinding;
import com.btl_ptit.hotelbooking.databinding.PopularDestinationItemBinding;
import com.btl_ptit.hotelbooking.listener.OnDestinationClickListener;
import com.btl_ptit.hotelbooking.listener.OnHotelClickListener;
import com.btl_ptit.hotelbooking.utils.Constants;
import com.btl_ptit.hotelbooking.utils.paging.MyComparator;
import com.btl_ptit.hotelbooking.view.activity.HotelDetailActivity;
import com.btl_ptit.hotelbooking.view.adapter.HotelAdapter;
import com.btl_ptit.hotelbooking.view.adapter.LoadStateAdapter;
import com.btl_ptit.hotelbooking.view.adapter.PopularDestinationAdapter;
import com.btl_ptit.hotelbooking.view_model.paging.HotelViewModel;
import com.btl_ptit.hotelbooking.view_model.paging.HotelViewModelFactory;
import com.btl_ptit.hotelbooking.view_model.paging.PopularDestinationViewModel;
import com.btl_ptit.hotelbooking.view_model.paging.PopularDestinationViewModelFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentHomeBinding mFragmentHomeBinding;
    private Context mContext;
    private String TAG = "HomeFragmentTAG";
    private GoogleMap googleMap;
    private PopularDestinationAdapter mPopularDestinationAdapter;
    private PopularDestinationViewModel mPopularDestinationViewModel;
    private MyDestinationRestService mMyDestinationRestService;
    private MyDestinationRepository mMyDestinationRepository;
    private MyHotelRepository mMyHotelRepository;
    private HotelRestService mHotelRestService;
    private HotelViewModel mHotelViewModel;
    private HotelAdapter mHotelAdapter;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        if (rootView == null) { // Lần đầu tiên khởi tạo
//            mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
//            rootView = mFragmentHomeBinding.getRoot();
//            mContext = getContext();
//
//            initPopularDestinations();
//            initRecommendedHotels();
//            initListeners();
//        } else {
//            // tránh "View already has a parent"
//            if (rootView.getParent() != null) {
//                ((ViewGroup) rootView.getParent()).removeView(rootView);
//            }
//            rebindAdapters();
//        }
//
//        observePagingData();
//        if (googleMap == null) {
//            setupMap();
//        }

        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        View rootView = mFragmentHomeBinding.getRoot();
        mContext = getContext();

        initListeners();

        initPopularDestinations();
        initRecommendedHotels();

        return rootView;
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        observePagingData();
//    }
//
//    private void observePagingData() {
//        compositeDisposable.clear();
//    }

    private void initListeners() {
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

        // tắt việc bắt sự kiện ở các view cha của layout map khi thao tác với layout chứa map
        mFragmentHomeBinding.mapTouchInterceptor.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.getParent().requestDisallowInterceptTouchEvent(false);
            } else {
                v.getParent().requestDisallowInterceptTouchEvent(true);
            }
            return false;
        });
    }

//    private void rebindAdapters() {
//        if (mPopularDestinationAdapter == null) {
//            initPopularDestinations();
//        }
//        if (mHotelAdapter == null) {
//            initRecommendedHotels();
//        }
//        if (mFragmentHomeBinding.recyclerViewDestinations.getAdapter() == null) {
//            mFragmentHomeBinding.recyclerViewDestinations.setAdapter(mPopularDestinationAdapter);
//        }
//        if (mFragmentHomeBinding.recyclerViewHotels.getAdapter() == null) {
//            mFragmentHomeBinding.recyclerViewHotels.setAdapter(mHotelAdapter);
//        }
//    }


    private void initPopularDestinations() {
        mMyDestinationRestService = MockApiClient.createService(MyDestinationRestService.class);
        mMyDestinationRepository = new MyDestinationRepository(mMyDestinationRestService);
        mPopularDestinationViewModel = new ViewModelProvider(requireActivity(), new PopularDestinationViewModelFactory(mMyDestinationRepository)).get(PopularDestinationViewModel.class);

        initDestinationAdapter();

        compositeDisposable.add(
            mPopularDestinationViewModel.pagingDataFlow
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pagingData -> mPopularDestinationAdapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData))
        );
    }

    private void initDestinationAdapter() {
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

        if (mPopularDestinationAdapter.getItemCount() == 0 && mFragmentHomeBinding.shimmerContainer.getChildCount() == 0) {
            setupShimmerPlaceholder();
        }

        mPopularDestinationAdapter.addLoadStateListener(loadState -> {
            if (loadState.getRefresh() instanceof LoadState.Loading || loadState.getRefresh() instanceof LoadState.Error) {
                mFragmentHomeBinding.shimmerContainer.setVisibility(View.VISIBLE);
                mFragmentHomeBinding.recyclerViewDestinations.setVisibility(View.GONE);
                Log.d(TAG, "mPopularDestinationAdapter loading!");
            } else {
//                Log.d(TAG, mPopularDestinationAdapter.getItemCount() + " mPopularDestinationAdapter ");
                mFragmentHomeBinding.recyclerViewDestinations.setVisibility(View.VISIBLE);
                mFragmentHomeBinding.shimmerContainer.setVisibility(View.GONE);
                Log.d(TAG, "mPopularDestinationAdapter not load!");
            }
            return null;
        });
    }

    private void setupShimmerPlaceholder() { // set shimmer place holder với số lượng dựa vào chiều rộng màn hình
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
        Log.d(TAG, "setupShimmerPlaceholder!");
    }

    private void initRecommendedHotels() {
        mHotelRestService = MockApiClient.createService(HotelRestService.class);
        mMyHotelRepository = new MyHotelRepository(mHotelRestService);
        mHotelViewModel = new ViewModelProvider(requireActivity(), new HotelViewModelFactory(mMyHotelRepository, true)).get(HotelViewModel.class);

        initHotelAdapter();

        compositeDisposable.add(
            mHotelViewModel.pagingDataFlow
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pagingData -> mHotelAdapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData))
        );
    }

    private void initHotelAdapter() {
        mHotelAdapter = new HotelAdapter(new MyComparator<MyHotel>(), requireContext(), new OnHotelClickListener() {
            @Override
            public void onHotelClick(MyHotel myHotel) {
                Intent intent = new Intent(mContext, HotelDetailActivity.class);
                intent.putExtra("hotel_id", myHotel.getId()); // Truyền ID sang để bên kia gọi API
                intent.putExtra("hotel_name", myHotel.getName());
                startActivity(intent);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
        );

        mFragmentHomeBinding.recyclerViewHotels.setLayoutManager(layoutManager);
        mFragmentHomeBinding.recyclerViewHotels.setHasFixedSize(true);
        mFragmentHomeBinding.recyclerViewHotels.setAdapter(mHotelAdapter);
        mFragmentHomeBinding.recyclerViewHotels.setNestedScrollingEnabled(false);

        if (mHotelAdapter.getItemCount() == 0 && mFragmentHomeBinding.shimmerContainer2.getChildCount() == 0) {
            setupShimmerPlaceholder(Constants.NUM_OF_PLACE_HOLDER);

        }

        mHotelAdapter.addLoadStateListener(loadState -> {
            if (loadState.getRefresh() instanceof LoadState.Loading || loadState.getRefresh() instanceof LoadState.Error) {
                mFragmentHomeBinding.mapFragmentLayout.setVisibility(View.VISIBLE);
                mFragmentHomeBinding.shimmerContainer2.setVisibility(View.VISIBLE);
                mFragmentHomeBinding.recyclerViewHotels.setVisibility(View.GONE);
                Log.d(TAG, "mHotelAdapter loading!");
            } else {
//                Log.d(TAG, mHotelAdapter.getItemCount() + " mHotelAdapter ");
                mFragmentHomeBinding.recyclerViewHotels.setVisibility(View.VISIBLE);
                mFragmentHomeBinding.shimmerContainer2.setVisibility(View.GONE);
                mFragmentHomeBinding.mapFragmentLayout.setVisibility(View.VISIBLE);
                if (googleMap == null) {
                    setupMap();
                }
                Log.d(TAG, "mHotelAdapter not load!");
            }
            return null;
        });
    }

    private void setupShimmerPlaceholder(int numOfPlaceHolder) { // set shimmer place holder với số lượng cố định
        mFragmentHomeBinding.shimmerContainer2.removeAllViews();
        for (int i = 0; i < numOfPlaceHolder; i++) {
            // Inflate item
            HotelItemBinding shimmerBinding = HotelItemBinding.inflate(getLayoutInflater(), mFragmentHomeBinding.shimmerContainer2, false);

            if (shimmerBinding.shimmerLayout != null) {
                shimmerBinding.shimmerLayout.startShimmer();
            }

            mFragmentHomeBinding.shimmerContainer2.addView(shimmerBinding.getRoot());
        }
        Log.d(TAG, "setupShimmerPlaceholder!");
    }


    private void setupMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.d(TAG, "MapFragment not found in layout");
        }
        Log.d(TAG, "setupMap called");
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        Log.d(TAG, "onMapReady called");

        LatLng hotel = new LatLng(20.9788183,105.7866833); // example
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(hotel, 14f));
        map.addMarker(new MarkerOptions().position(hotel).title("Hotel"));

        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        map.setOnMapLoadedCallback(() -> Log.d(TAG, "Google Map loaded"));
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.clear();
//        googleMap = null;
        mFragmentHomeBinding = null;
        super.onDestroyView();
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//    }
}
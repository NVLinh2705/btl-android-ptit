package com.btl_ptit.hotelbooking.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;
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
import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.btl_ptit.hotelbooking.databinding.FragmentHomeBinding;
import com.btl_ptit.hotelbooking.databinding.HotelItemBinding;
import com.btl_ptit.hotelbooking.databinding.PopularDestinationItemBinding;
import com.btl_ptit.hotelbooking.listener.OnDestinationClickListener;
import com.btl_ptit.hotelbooking.listener.OnHotelClickListener;
import com.btl_ptit.hotelbooking.utils.Constants;
import com.btl_ptit.hotelbooking.utils.MyUtils;
import com.btl_ptit.hotelbooking.utils.paging.MyComparator;
import com.btl_ptit.hotelbooking.view.activity.HotelDetailActivity;
import com.btl_ptit.hotelbooking.view.activity.HotelInfoActivity;
import com.btl_ptit.hotelbooking.view.activity.ListDestinationActivity;
import com.btl_ptit.hotelbooking.view.activity.MyMapActivity;
import com.btl_ptit.hotelbooking.view.activity.SearchActivity;
import com.btl_ptit.hotelbooking.view.adapter.HotelAdapter;
import com.btl_ptit.hotelbooking.view.adapter.LoadStateAdapter;
import com.btl_ptit.hotelbooking.view.adapter.PopularDestinationAdapter;
import com.btl_ptit.hotelbooking.view_model.OccupancyViewModel;
import com.btl_ptit.hotelbooking.view_model.paging.HotelViewModel;
import com.btl_ptit.hotelbooking.view_model.paging.HotelViewModelFactory;
import com.btl_ptit.hotelbooking.view_model.paging.PopularDestinationViewModel;
import com.btl_ptit.hotelbooking.view_model.paging.PopularDestinationViewModelFactory;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentHomeBinding mFragmentHomeBinding;
    private Context mContext;
    private String TAG = "HomeFragmentTAG";
    private long checkInDate;
    private long checkOutDate;
    private GoogleMap googleMap;
    private PopularDestinationAdapter mPopularDestinationAdapter;
    private PopularDestinationViewModel mPopularDestinationViewModel;
    private MyDestinationRestService mMyDestinationRestService;
    private MyDestinationRepository mMyDestinationRepository;
    private MyHotelRepository mMyHotelRepository;
    private HotelRestService mHotelRestService;
    private HotelViewModel mHotelViewModel;
    private HotelAdapter mHotelAdapter;

    private OccupancyViewModel occupancyViewModel;
    private View rootView;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    enableMyLocation();
                } else {
                    if (!shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Toast.makeText(mContext, R.string.labelBlockYourLocation, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, R.string.labelRequireYourLocation, Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        initDefaultDates();
        initSessionValues();
        initOccupancyViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
            rootView = mFragmentHomeBinding.getRoot();
            mContext = getContext();

            initListeners();

            initPopularDestinations();
            initRecommendedHotels();
        } else {
            // tránh "View already has a parent"
            if (rootView.getParent() != null) {
                ((ViewGroup) rootView.getParent()).removeView(rootView);
            }
            rebindAdapters();
        }
        observeOccupancyViewModel();
        setupMap();
        return rootView;
    }

    private void initOccupancyViewModel() {
        occupancyViewModel = new ViewModelProvider(requireActivity()).get(OccupancyViewModel.class);
    }

    private void observeOccupancyViewModel() {
        if (occupancyViewModel == null) {
            initOccupancyViewModel();
        }
//        occupancyViewModel.getPersons().observe(getViewLifecycleOwner(), count -> {
//            mFragmentHomeBinding.chipGuests.setText(count + " " + mContext.getString(R.string.person_txt));
//        });

        occupancyViewModel.getRooms().observe(getViewLifecycleOwner(), count -> {
            mFragmentHomeBinding.chipRooms.setText(count + " " + mContext.getString(R.string.room_txt));
        });

        occupancyViewModel.getDoubleBed().observe(getViewLifecycleOwner(), count -> {
            if (count == 0) {
                mFragmentHomeBinding.chipDoubleBeds.setVisibility(View.GONE);
                return;
            } else {
                SessionManager.getInstance().setNumAdults(count);
                mFragmentHomeBinding.chipDoubleBeds.setVisibility(View.VISIBLE);
                mFragmentHomeBinding.chipDoubleBeds.setText(count + " " + mContext.getString(R.string.adult));
            }
        });

        occupancyViewModel.getSingleBed().observe(getViewLifecycleOwner(), count -> {
            SessionManager.getInstance().setNumChildren(count);
            if (count == 0) {
                mFragmentHomeBinding.chipSingleBeds.setVisibility(View.GONE);
                return;
            } else {
                mFragmentHomeBinding.chipSingleBeds.setVisibility(View.VISIBLE);
                mFragmentHomeBinding.chipSingleBeds.setText(count + " " + mContext.getString(R.string.children));
            }
        });

        occupancyViewModel.getSelectedLocation().observe(getViewLifecycleOwner(), location -> {
            if (location != null && !location.isEmpty()) {
                mFragmentHomeBinding.tvLocationSelected.setText(location);
            } else {
                mFragmentHomeBinding.tvLocationSelected.setText(R.string.where_to_go_label);
            }
        });
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
        Glide.with(mFragmentHomeBinding.imgProfile.getContext())
                .load(SessionManager.getInstance().getUser().getAvatarUrl())
                .into(mFragmentHomeBinding.imgProfile);
        mFragmentHomeBinding.txtName.setText(SessionManager.getInstance().getUser().getFullName());
        mFragmentHomeBinding.tvCheckInDate.setText(MyUtils.myFormatDate(checkInDate));
        mFragmentHomeBinding.tvCheckOutDate.setText(MyUtils.myFormatDate(checkOutDate));

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

        mFragmentHomeBinding.btnZoomMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MyMapActivity.class);
                startActivity(intent);
            }
        });

        mFragmentHomeBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, SearchActivity.class));
            }
        });

        mFragmentHomeBinding.btnDatePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
                constraintsBuilder.setValidator(DateValidatorPointForward.now());

                MaterialDatePicker<Pair<Long, Long>> dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText(R.string.title_date_picker)
                        .setCalendarConstraints(constraintsBuilder.build())
                        .setSelection(new Pair<>(checkInDate, checkOutDate)) // Truyền giá trị hiện tại
                        .build();

                dateRangePicker.show(getParentFragmentManager(), "DATE_PICKER");

                dateRangePicker.addOnPositiveButtonClickListener(selection -> {
                    checkInDate = selection.first;
                    checkOutDate = selection.second;
                    SessionManager.getInstance().setCheckinDate(MyUtils.myFormatDateForSessionManager(checkInDate));
                    SessionManager.getInstance().setCheckoutDate(MyUtils.myFormatDateForSessionManager(checkOutDate));
                    mFragmentHomeBinding.tvCheckInDate.setText(MyUtils.myFormatDate(checkInDate));
                    mFragmentHomeBinding.tvCheckOutDate.setText(MyUtils.myFormatDate(checkOutDate));
                });
            }
        });

        mFragmentHomeBinding.btnOccupancyPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchRoomBottomSheet bottomSheet = new SearchRoomBottomSheet();
                bottomSheet.show(getParentFragmentManager(), "Occupancy filter");
            }
        });

        mFragmentHomeBinding.btnLocationPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyLocationBottomSheet bottomSheet = new MyLocationBottomSheet();
                bottomSheet.show(getParentFragmentManager(), "Location filter");
            }
        });

        mFragmentHomeBinding.tvViewDestinationsSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ListDestinationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void rebindAdapters() {
        if (mPopularDestinationAdapter == null) {
            initPopularDestinations();
        }
        if (mHotelAdapter == null) {
            initRecommendedHotels();
        }
        if (mFragmentHomeBinding.recyclerViewDestinations.getAdapter() == null) {
            mFragmentHomeBinding.recyclerViewDestinations.setAdapter(mPopularDestinationAdapter);
        }
        if (mFragmentHomeBinding.recyclerViewHotels.getAdapter() == null) {
            mFragmentHomeBinding.recyclerViewHotels.setAdapter(mHotelAdapter);
        }
    }


    private void initPopularDestinations() {
        initDestinationAdapter();
        if (mPopularDestinationAdapter != null) {
            mMyDestinationRestService = MockApiClient.createService(MyDestinationRestService.class);
            mMyDestinationRepository = new MyDestinationRepository(mMyDestinationRestService);
            mPopularDestinationViewModel = new ViewModelProvider(requireActivity(), new PopularDestinationViewModelFactory(mMyDestinationRepository)).get(PopularDestinationViewModel.class);

            compositeDisposable.add(
                mPopularDestinationViewModel.pagingDataFlow
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(pagingData -> mPopularDestinationAdapter.submitData(getLifecycle(), pagingData))
            );
        }
    }

    private void initDestinationAdapter() {
        if (mPopularDestinationAdapter == null) {
            mPopularDestinationAdapter = new PopularDestinationAdapter(new MyComparator<MyPopularDestination>(), requireContext(), new OnDestinationClickListener() {
                @Override
                public void onDestinationClick(MyPopularDestination destination) {
                    Intent intent = new Intent(mContext, ListDestinationActivity.class);
                    startActivity(intent);
                }
            });
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );

        if (mFragmentHomeBinding.recyclerViewDestinations.getLayoutManager() == null) {
            mFragmentHomeBinding.recyclerViewDestinations.setLayoutManager(layoutManager);
            mFragmentHomeBinding.recyclerViewDestinations.setHasFixedSize(true);

            mFragmentHomeBinding.recyclerViewDestinations.setAdapter(
                    mPopularDestinationAdapter.withLoadStateFooter(
                            new LoadStateAdapter(v -> mPopularDestinationAdapter.retry())
                    )
            );
        }

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
        initHotelAdapter();
        if (mHotelAdapter != null) {
            mHotelRestService = MockApiClient.createService(HotelRestService.class);
            mMyHotelRepository = new MyHotelRepository(mHotelRestService);
            mHotelViewModel = new ViewModelProvider(requireActivity(), new HotelViewModelFactory(mMyHotelRepository, true)).get(HotelViewModel.class);

            compositeDisposable.add(
                    mHotelViewModel.pagingDataFlow
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(pagingData -> mHotelAdapter.submitData(getLifecycle(), pagingData))
            );
        }
    }

    private void initHotelAdapter() {
        if (mHotelAdapter == null) {
            mHotelAdapter = new HotelAdapter(new MyComparator<MyHotel>(), requireContext(), new OnHotelClickListener() {
                @Override
                public void onHotelClick(MyHotel myHotel) {
                    Intent intent = new Intent(mContext, HotelInfoActivity.class);
                    int hotelId = myHotel.getId();
                    intent.putExtra(Constants.HOTEL_ID, hotelId);
                    Log.d(TAG, "hotel ID: " + hotelId);
                    startActivity(intent);
                }
            });
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
        );

        if (mFragmentHomeBinding.recyclerViewHotels.getLayoutManager() == null) {
            mFragmentHomeBinding.recyclerViewHotels.setLayoutManager(layoutManager);
            mFragmentHomeBinding.recyclerViewHotels.setHasFixedSize(false);
            mFragmentHomeBinding.recyclerViewHotels.setAdapter(mHotelAdapter);
            mFragmentHomeBinding.recyclerViewHotels.setNestedScrollingEnabled(false);
        }

        if (mHotelAdapter.getItemCount() == 0 && mFragmentHomeBinding.shimmerContainer2.getChildCount() == 0) {
            setupShimmerPlaceholder(Constants.NUM_OF_PLACE_HOLDER);
        }

        mHotelAdapter.addLoadStateListener(loadState -> {
            if (loadState.getRefresh() instanceof LoadState.Loading || loadState.getRefresh() instanceof LoadState.Error) {
                mFragmentHomeBinding.shimmerContainer2.setVisibility(View.VISIBLE);
                mFragmentHomeBinding.recyclerViewHotels.setVisibility(View.GONE);
                Log.d(TAG, "mHotelAdapter loading!");
            } else {
//                Log.d(TAG, mHotelAdapter.getItemCount() + " mHotelAdapter ");
                mFragmentHomeBinding.recyclerViewHotels.setVisibility(View.VISIBLE);
                mFragmentHomeBinding.shimmerContainer2.setVisibility(View.GONE);
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
        if (mapFragment == null) {
            Log.d(TAG, "MapFragment not found in layout");
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.mapFragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        Log.d(TAG, "setupMap called");
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        Log.d(TAG, "onMapReady called");

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        map.getUiSettings().setZoomControlsEnabled(false); // Dùng cử chỉ zoom
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        // Kích hoạt vị trí
        enableMyLocation();
        mFragmentHomeBinding.mapFragmentLayout.setVisibility(View.VISIBLE);
        map.setOnMapLoadedCallback(() -> Log.d(TAG, "Google Map loaded"));
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                // Hiện chấm xanh và nút "My Location" mặc định
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                // Lấy vị trí để di chuyển Camera đến đó 1 lần duy nhất lúc đầu
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, Constants.ZOOM_LEVEL));
                    }
                });
            }
        } else {
            // Kiểm tra xem người dùng đã từng từ chối chưa
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new androidx.appcompat.app.AlertDialog.Builder(mContext)
                        .setMessage(R.string.labelRequireYourLocation)
                        .setPositiveButton(R.string.app_name, (d, w) -> {
                            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
                        })
                        .show();
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }

    private void initDefaultDates() {
        // 1. Khởi tạo Calendar cho ngày hôm nay (Check-in)
        checkInDate = MaterialDatePicker.todayInUtcMilliseconds();

        // 2. Để tính "Ngày mai", chúng ta dùng Calendar nhưng ép về múi giờ UTC
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(checkInDate);
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        checkOutDate = calendar.getTimeInMillis();
    }
    private void initSessionValues() {
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.setCheckinDate(MyUtils.myFormatDateForSessionManager(checkInDate));
        sessionManager.setCheckoutDate(MyUtils.myFormatDateForSessionManager(checkOutDate));
        sessionManager.setNumAdults(1);
        sessionManager.setNumChildren(1);
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.clear();
//        googleMap = null;
//        mFragmentHomeBinding = null;
        super.onDestroyView();
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//    }
}
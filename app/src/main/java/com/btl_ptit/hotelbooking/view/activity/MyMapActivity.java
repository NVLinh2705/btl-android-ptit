package com.btl_ptit.hotelbooking.view.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.dto.HotelInBoundResponse;
import com.btl_ptit.hotelbooking.data.dto.MapInBoundsParams;
import com.btl_ptit.hotelbooking.data.remote.MapServiceClient;
import com.btl_ptit.hotelbooking.data.remote.MockApiClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.HotelRestService;
import com.btl_ptit.hotelbooking.data.remote.api_services.MyMapService;
import com.btl_ptit.hotelbooking.data.repository.MyHotelRepository;
import com.btl_ptit.hotelbooking.data.repository.MyMapRepository;
import com.btl_ptit.hotelbooking.databinding.ActivityMyMapBinding;
import com.btl_ptit.hotelbooking.utils.Constants;
import com.btl_ptit.hotelbooking.utils.MyUtils;
import com.btl_ptit.hotelbooking.view.adapter.HotelsInBoundAdapter;
import com.btl_ptit.hotelbooking.view.fragment.FilterBottomSheet;
import com.btl_ptit.hotelbooking.view_model.MyMapViewModel;
import com.btl_ptit.hotelbooking.view_model.factory.MyMapViewModelFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private ActivityMyMapBinding mActivityMyMapBinding;
    private String TAG = "MyMapActivityTAG";
    private Context mContext;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private MyMapRepository mMyMapRepository;
//    private MyHotelRepository mMyHotelRepository;
//    private HotelRestService mHotelRestService;
    private MyMapService mMyMapService;
    private MyMapViewModel mMyMapViewModel;
    private HotelsInBoundAdapter mHotelsInBoundAdapter;
    private int cnt = 1;
    private Marker selectedMarker;
    private int currentScrollState = ViewPager2.SCROLL_STATE_IDLE;
    private HashMap<String, Marker> markerMap = new HashMap<>();

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        mActivityMyMapBinding = ActivityMyMapBinding.inflate(getLayoutInflater());
        setContentView(mActivityMyMapBinding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(mActivityMyMapBinding.btnBack, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) v.getLayoutParams();
            params.topMargin = insets.top + (int)(8 * getResources().getDisplayMetrics().density);
            v.setLayoutParams(params);
            return windowInsets;
        });

        mContext = this;
        initListeners();

        setupMap();
        setupViewPager();

        initMyMapViewModel();
        observeHotelsFromViewModel();
    }

    private void initMyMapViewModel() {
        mMyMapService = MapServiceClient.createService(MyMapService.class);
        mMyMapRepository = new MyMapRepository(mMyMapService);
        mMyMapViewModel = new ViewModelProvider(this, new MyMapViewModelFactory(mMyMapRepository)).get(MyMapViewModel.class);
    }

    private void observeHotelsFromViewModel() {
        mMyMapViewModel.getHotels().observe(this, hotels -> {
            if (hotels != null && !hotels.isEmpty()) {
                MyUtils.showViewPager(mActivityMyMapBinding.viewPagerHotels);
                mHotelsInBoundAdapter.updateData(hotels);
                // Ép ViewPager2 tính toán lại toàn bộ hiệu ứng cho danh sách mới
                mActivityMyMapBinding.viewPagerHotels.requestTransform();
                // tạo hiệu ứng kéo giả trigger các transform
                mActivityMyMapBinding.viewPagerHotels.post(() -> {
                    mActivityMyMapBinding.viewPagerHotels.beginFakeDrag();
                    mActivityMyMapBinding.viewPagerHotels.fakeDragBy(2f);
                    mActivityMyMapBinding.viewPagerHotels.endFakeDrag();
                });
                // Thêm Marker lên bản đồ
                displayHotelsOnMap(hotels);
            } else {
                mActivityMyMapBinding.viewPagerHotels.setVisibility(View.GONE);
            }
        });

        mMyMapViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                cnt++;
            }
            Log.d(TAG, "isLoading: " + isLoading + " cnt: " + cnt);
        });

    }

    private void initListeners() {
        selectedMarker = null;
        mActivityMyMapBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mActivityMyMapBinding.btnMyLocation.setOnClickListener(v -> {
            enableMyLocation();
        });

        mActivityMyMapBinding.btnInnerFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterBottomSheet filterSheet = new FilterBottomSheet();
                filterSheet.show(getSupportFragmentManager(), "Filter");
            }
        });
    }

    private void setupViewPager() {
        // set adapter
        mHotelsInBoundAdapter = new HotelsInBoundAdapter(new ArrayList<>(), mContext);
        mActivityMyMapBinding.viewPagerHotels.setAdapter(mHotelsInBoundAdapter);

        // Để lộ một phần của card trước và sau
        mActivityMyMapBinding.viewPagerHotels.setOffscreenPageLimit(3);
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(MyUtils.dpToPx(0)));
        int nextItemVisiblePx = MyUtils.dpToPx(7);
        int currentItemHorizontalMarginPx = MyUtils.dpToPx(15);
        int pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx;
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.80f + r * 0.20f); // Card giữa to hơn card hai bên
            page.setTranslationX(-1 * pageTranslationX * position); // kéo 2 card 2 bên vào sát card giữa
            page.setTranslationZ(r * 10f);  // tăng độ nổi card giữa
            page.setAlpha(0.6f + r * 0.4f); // Card bên cạnh hơi mờ đi để tránh rối mắt
        });
        mActivityMyMapBinding.viewPagerHotels.setPageTransformer(transformer);

        // lắng nghe sự kiện khi vuốt view pager
        mActivityMyMapBinding.viewPagerHotels.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                currentScrollState = state;
            }

            @Override
            public void onPageSelected(int position) {
                // CHỈ cập nhật Marker nếu người dùng đang thực sự VUỐT (DRAGGING)
                // hoặc đang trượt do lực vuốt (SETTLING)
                if (currentScrollState != ViewPager2.SCROLL_STATE_IDLE) {
                    Log.d(TAG, "onPageSelected: " + position);
                    HotelInBoundResponse hotel = mHotelsInBoundAdapter.getItems().get(position);
                    Marker targetMarker = markerMap.get(hotel.getId());

                    if (targetMarker != null) {
                        // Reset marker cũ
                        if (selectedMarker != null) {
                            updateMarkerAppearance(selectedMarker, false, false);
                        }

                        // Highlight marker mới
                        updateMarkerAppearance(targetMarker, true, true);
                        selectedMarker = targetMarker;

                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(targetMarker.getPosition()));
                    } else {
                        // Reset marker cũ
                        if (selectedMarker != null) {
                            updateMarkerAppearance(selectedMarker, false, false);
                        }
                    }
                }
            }
        });
    }

    private void setupMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myMapFragment);
        if (mapFragment == null) {
            Log.d(TAG, "MapFragment not found in layout");
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.myMapFragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        Log.d(TAG, "setupMap called");
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        Log.d(TAG, "onMapReady called");

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        map.getUiSettings().setZoomControlsEnabled(false); // Dùng cử chỉ zoom
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        // Kích hoạt vị trí
        enableMyLocation();
        map.setOnMapLoadedCallback(() -> Log.d(TAG, "Google Map loaded"));

        googleMap.setOnCameraIdleListener(() -> {
            LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
            float zoom = googleMap.getCameraPosition().zoom;

            Log.d(TAG, "onCameraIdle called: " + bounds.toString() + " zoom level: " + zoom);

            mMyMapViewModel.onMapChanged(new MapInBoundsParams(bounds, zoom, cnt, 10));
        });

        googleMap.setOnMarkerClickListener(this);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                // Kiểm tra xem ViewPager có đang hiển thị không
                if (mActivityMyMapBinding.viewPagerHotels.getVisibility() == View.VISIBLE) {
                    // Hiệu ứng ẩn
                    MyUtils.hideViewPager(mActivityMyMapBinding.viewPagerHotels);
                } else {
                    // Nếu đang ẩn thì hiện lại
                    MyUtils.showViewPager(mActivityMyMapBinding.viewPagerHotels);
                }
            }
        });

        // Khi Camera bắt đầu di chuyển (do người dùng kéo hoặc zoom)
//        googleMap.setOnCameraMoveStartedListener(reason -> {
//            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
//                // Chỉ ẩn khi người dùng chạm tay vào kéo (không ẩn khi code tự move)
//                mActivityMyMapBinding.viewPagerHotels.animate()
//                        .translationY(mActivityMyMapBinding.viewPagerHotels.getHeight()) // Đẩy xuống dưới
//                        .alpha(0f)
//                        .setDuration(300)
//                        .start();
//            }
//        });
//
//        // Khi Camera đã đứng yên
//        googleMap.setOnCameraIdleListener(() -> {
//            mActivityMyMapBinding.viewPagerHotels.animate()
//                    .translationY(0) // Kéo lên lại
//                    .alpha(1f)
//                    .setDuration(300)
//                    .start();
//        });
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                // Hiện chấm xanh mặc định
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);

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
    private void displayHotelsOnMap(List<HotelInBoundResponse> hotels) {
        if (googleMap == null) return;
        googleMap.clear(); // Xóa các marker cũ khi map di chuyển/cập nhật dữ liệu mới
        selectedMarker = null; // Reset khi map load data mới
        markerMap.clear();

        // 1. Lấy vị trí tâm hiện tại
        LatLng center = googleMap.getCameraPosition().target;
        double lat = center.latitude;
        double lng = center.longitude;

        // 2. Định nghĩa độ lệch (offset) - khoảng 500m đến 1km
        double offset = 0.005;

        // 3. Danh sách 4 vị trí giả (Đông, Tây, Nam, Bắc)
        LatLng pos1 = new LatLng(lat + offset, lng);          // Bắc
        LatLng pos2 = new LatLng(lat - offset, lng);          // Nam
        LatLng pos3 = new LatLng(lat, lng + offset);          // Đông
        LatLng pos4 = new LatLng(lat, lng - offset);          // Tây

        Marker marker1 = googleMap.addMarker(new MarkerOptions()
                .position(pos1)
                .icon(MyUtils.createMarkerIcon(mContext, "₫ " + hotels.get(0).getAveragePrice() * 100000, false, false)) // Selected
                .anchor(0.5f, 1.0f)
                .zIndex(1.0f));
        marker1.setTag(hotels.get(0));
        markerMap.put(hotels.get(0).getId(), marker1);

        Marker marker2 = googleMap.addMarker(new MarkerOptions()
                .position(pos2)
                .icon(MyUtils.createMarkerIcon(mContext, "₫ " + hotels.get(1).getAveragePrice() * 100000, false, false))
                .anchor(0.5f, 1.0f)
                .zIndex(1.0f));
        marker2.setTag(hotels.get(1));
        markerMap.put(hotels.get(1).getId(), marker2);

        Marker marker3 = googleMap.addMarker(new MarkerOptions()
                .position(pos3)
                .icon(MyUtils.createMarkerIcon(mContext, "₫ " + hotels.get(2).getAveragePrice() * 100000, false, true))
                .anchor(0.5f, 1.0f)
                .zIndex(1.0f));
        marker3.setTag(hotels.get(2));
        markerMap.put(hotels.get(2).getId(), marker3);


        Marker marker4 = googleMap.addMarker(new MarkerOptions()
                .position(pos4)
                .icon(MyUtils.createMarkerIcon(mContext, "₫ " + hotels.get(3).getAveragePrice() * 100000, false, false))
                .anchor(0.5f, 1.0f)
                .zIndex(1.0f));
        marker4.setTag(hotels.get(3));
        markerMap.put(hotels.get(3).getId(), marker4);


//        for (HotelInBoundResponse hotel : hotels) {
//            LatLng position = new LatLng(hotel.getLatitude(), hotel.getLongitude());
//            String formattedPrice = "₫ " + MyUtils.formatPrice(hotel.getAveragePrice());
//
//            googleMap.addMarker(new MarkerOptions()
//                    .position(position)
//                    .icon(MyUtils.createMarkerIcon(mContext, formattedPrice, false))
//                    .anchor(0.5f, 1.0f));
//        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        HotelInBoundResponse hotel = (HotelInBoundResponse) marker.getTag();

        if (hotel != null) {
            // 1. Reset marker cũ về trạng thái bình thường
            if (selectedMarker != null && !selectedMarker.equals(marker)) {
                updateMarkerAppearance(selectedMarker, false, false);
            }

            // 2. Cập nhật marker vừa click thành trạng thái "Selected"
            updateMarkerAppearance(marker, true, true);
            selectedMarker = marker;

            // 2. Đồng bộ ViewPager2
            int position = mHotelsInBoundAdapter.getItems().indexOf(hotel);
            if (position != -1) {
                mActivityMyMapBinding.viewPagerHotels.setCurrentItem(position, true);
            }
            MyUtils.showViewPager(mActivityMyMapBinding.viewPagerHotels);
        }

        return false;
    }

    private void updateMarkerAppearance(Marker marker, boolean isSelected, boolean isFavourite) {
        HotelInBoundResponse hotel = (HotelInBoundResponse) marker.getTag();
        if (hotel == null) return;

        Log.d(TAG, "marker onClick: " + hotel.getName() + " price: " + hotel.getAveragePrice());
//        String formattedPrice = "₫ " + hotel.getAveragePrice();
//
        // Tạo icon mới dựa trên trạng thái
        marker.setIcon(MyUtils.createMarkerIcon(mContext, "₫ " + hotel.getAveragePrice() * 100000, isSelected, isFavourite));

        if (isSelected) {
            marker.setZIndex(10.0f); // Đưa lên trên cùng
        } else {
            marker.setZIndex(1.0f);   // Trả về lớp dưới
        }
    }
}
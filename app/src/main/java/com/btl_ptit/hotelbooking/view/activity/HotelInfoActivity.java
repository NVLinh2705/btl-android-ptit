package com.btl_ptit.hotelbooking.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.dto.HotelImage;
import com.btl_ptit.hotelbooking.data.dto.HotelResponse;
import com.btl_ptit.hotelbooking.data.dto.LikeHotelRequest;
import com.btl_ptit.hotelbooking.data.session.RoomSelectionStore;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.SupabaseRestService;
import com.btl_ptit.hotelbooking.databinding.ActivityHotelInfoBinding;
import com.btl_ptit.hotelbooking.view.adapter.FacilityAdapter;
import com.btl_ptit.hotelbooking.view.adapter.HotelImagePagerAdapter;
import com.btl_ptit.hotelbooking.view.adapter.PolicyAdapter;
import com.btl_ptit.hotelbooking.view.adapter.ReviewSummaryAdapter;
import com.google.gson.Gson;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotelInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "HotelInfoActivity";
    ActivityHotelInfoBinding b;

    private GoogleMap googleMap;

    private android.graphics.drawable.Drawable favoriteFilledDrawable;

    private HotelResponse hotelResponse;

    private SupabaseRestService restService;

    private LinearLayout emptyState;
    private final DateTimeFormatter apiDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private final DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityHotelInfoBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        restService = SupabaseClient.createService(SupabaseRestService.class);

        setupToolbar();
        setupMap();
        setupBottomCta();
        fetchHotelDataAsync(3);
    }

    private void fetchHotelDataAsync(int hotelId) {
        restService.getHotelById(hotelId).enqueue(new Callback<HotelResponse>() {
            @Override
            public void onResponse(Call<HotelResponse> call, Response<HotelResponse> response) {
                if (isFinishing() || isDestroyed()) return;

                HotelResponse hotelResp = response.body();
                if (hotelResp == null) {
                    showEmptyState();
                    return;
                }

                hotelResponse = hotelResp;
                bindHotelData();
            }

            @Override
            public void onFailure(Call<HotelResponse> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                Log.e(TAG, "Failed to load hotel details", t);
                showEmptyState();
                Toast.makeText(HotelInfoActivity.this, "Failed to load hotel details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyState() {
        b.emptyState.setVisibility(android.view.View.VISIBLE);
        b.scrollView.setVisibility(android.view.View.GONE);
        b.bottomBar.setVisibility(android.view.View.GONE);
        b.toolbar.setTitle("");
    }

    private void bindHotelData() {
        b.emptyState.setVisibility(android.view.View.GONE);
        b.scrollView.setVisibility(android.view.View.VISIBLE);
        b.bottomBar.setVisibility(android.view.View.VISIBLE);

        setupImageSlider();
        setupFacilities();
        setupReviewSummaries();
        setupPolicies();
        updateToolbar();

        b.txtHotelName.setText(hotelResponse.getName());
        double avgRating = hotelResponse.getStats() != null ? (hotelResponse.getStats().getAvgRating() != null? hotelResponse.getStats().getAvgRating() : 0.0 ) : 0.0;
        b.txtAvgRating.setText(String.format("%.1f", avgRating));
        b.txtAvgRating1.setText(String.format("%.1f", avgRating));

        if (avgRating >= 8.0) {
            b.txtRatingLabel.setText("Rất tốt");
        } else if (avgRating >= 7.0) {
            b.txtRatingLabel.setText("Tốt");
        } else if (avgRating >= 6.0) {
            b.txtRatingLabel.setText("Trung bình");
        } else if (avgRating >= 5.0) {
            b.txtRatingLabel.setText("Dưới trung bình");
        } else {
            b.txtRatingLabel.setText("Kém");
        }

        int totalReviews = hotelResponse.getStats() != null ? hotelResponse.getStats().getTotalReviews() : 0;
        b.txtRatingCount.setText(String.format("(%d đánh giá)", totalReviews));
        b.txtAddress.setText(hotelResponse.getAddress());

        String checkin = "N/A";
        String checkout = "N/A";
        if (hotelResponse.getPolicies() != null) {
            checkin = hotelResponse.getPolicies().stream()
                    .filter(p -> "CHECKIN".equalsIgnoreCase(p.getTypeCode()))
                    .map(p -> p.getContent())
                    .findFirst()
                    .orElse("N/A");
            checkout = hotelResponse.getPolicies().stream()
                    .filter(p -> "CHECKOUT".equalsIgnoreCase(p.getTypeCode()))
                    .map(p -> p.getContent())
                    .findFirst()
                    .orElse("N/A");
        }

        b.txtCheckin.setText(String.format("Nhận phòng:\n%s", checkin));
        b.txtCheckout.setText(String.format("Trả phòng:\n%s", checkout));
        b.txtDescription.setText(hotelResponse.getDescription());

        if (googleMap != null) {
            onMapReady(googleMap);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_favorite) {
                if (hotelResponse == null) return true;
                hotelResponse.setLiked(!hotelResponse.isLiked());
                toggleFavorite(item);
                return true;
            } else if (id == R.id.action_share) {
                Toast.makeText(this, "Share clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void updateToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(hotelResponse != null ? hotelResponse.getName() : "");
        android.view.MenuItem favoriteItem = b.toolbar.getMenu().findItem(R.id.action_favorite);
        if (favoriteItem != null) {
            renderFavoriteIcon(favoriteItem);
        }
    }

    private void renderFavoriteIcon(android.view.MenuItem item) {
        boolean isLiked = hotelResponse != null && hotelResponse.isLiked();
        if (isLiked) {
            if (favoriteFilledDrawable == null) {
                android.graphics.drawable.Drawable d = ContextCompat.getDrawable(this, R.drawable.ic_heart_filled);
                if (d != null) {
                    d = DrawableCompat.wrap(d.mutate());
                    DrawableCompat.setTint(d, ContextCompat.getColor(this, R.color.color_likedHeartIcon));
                    favoriteFilledDrawable = d;
                }
            }
            if (favoriteFilledDrawable != null) {
                item.setIcon(favoriteFilledDrawable);
                item.setIconTintList(null);
            }
        } else {
            item.setIcon(R.drawable.ic_heart_outline);
            item.setIconTintList(null);
        }
    }

    private void toggleFavorite(android.view.MenuItem item) {
        if (hotelResponse == null) return;
        boolean isLiked = hotelResponse.isLiked();
        LikeHotelRequest likeHotelRequest = new LikeHotelRequest(hotelResponse.getId());
        Call<Void> call = restService.likeHotel(likeHotelRequest);
        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                renderFavoriteIcon(item);
                Toast.makeText(HotelInfoActivity.this, isLiked  ? "Added to favorites" : "Removed from favorites", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed to update favorites state ", t);
            }
        });
    }

    private void setupMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.w(TAG, "MapFragment not found in layout");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_hotel_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void setupImageSlider() {
        ViewPager2 pagerImages = b.pagerImages;

        HotelImagePagerAdapter adapter = new HotelImagePagerAdapter();
        pagerImages.setAdapter(adapter);

        List<String> imageUrls = hotelResponse != null ?
                hotelResponse
                        .getImages()
                        .stream()
                        .map(HotelImage::getUrl)
                        .collect(Collectors.toList())
                : null;

        adapter.submitList(imageUrls);

        int dotCount = Math.min(6, imageUrls == null ? 0 : imageUrls.size());
        setupImageDots(b.layoutImageDots, dotCount);
        updateImageDots(0, imageUrls == null ? 0 : imageUrls.size(), dotCount);

        pagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateImageDots(position, imageUrls == null ? 0 : imageUrls.size(), dotCount);
            }
        });
    }

    private void setupImageDots(LinearLayout container, int dotCount) {
        container.removeAllViews();
        for (int i = 0; i < dotCount; i++) {
            android.view.View dot = new android.view.View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpToPx(6), dpToPx(6));
            lp.setMargins(dpToPx(3), 0, dpToPx(3), 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(R.drawable.tab_indicator_dot);
            container.addView(dot);
        }
        container.setVisibility(dotCount > 1 ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void updateImageDots(int position, int imageCount, int dotCount) {
        if (dotCount <= 0) return;
        int activeIndex = imageCount <= dotCount
                ? position
                : Math.min(dotCount - 1, position * dotCount / imageCount);
        for (int i = 0; i < b.layoutImageDots.getChildCount(); i++) {
            b.layoutImageDots.getChildAt(i).setSelected(i == activeIndex);
        }
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private void setupFacilities() {
        RecyclerView rvFacilities = findViewById(R.id.rvFacilities);
        rvFacilities.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        FacilityAdapter adapter = new FacilityAdapter();
        rvFacilities.setAdapter(adapter);

        adapter.submitList(hotelResponse.getFacilities());

        findViewById(R.id.txtSeeAllFacilities).setOnClickListener(v -> openHotelDetailTabs(HotelDetailActivity.TAB_FACILITIES));
        findViewById(R.id.txtSeeAllReviews).setOnClickListener(v -> openHotelDetailTabs(HotelDetailActivity.TAB_REVIEWS));
    }

    private void openHotelDetailTabs(int tabIndex) {
        if (hotelResponse == null) return;
        String policiesJson = new Gson().toJson(hotelResponse.getPolicies());
        startActivity(HotelDetailActivity.createIntent(
                this,
                hotelResponse.getId(),
                hotelResponse.getName(),
                hotelResponse.getDescription(),
                policiesJson,
                tabIndex
        ));
    }
    private void setupReviewSummaries() {
        RecyclerView rvReviewSummaries = b.rvReviewSummaries;
        rvReviewSummaries.setLayoutManager(new LinearLayoutManager(this));

        ReviewSummaryAdapter adapter = new ReviewSummaryAdapter();
        rvReviewSummaries.setAdapter(adapter);

        adapter.submitList(hotelResponse != null ? hotelResponse.getReviews() : null);
    }

    private void setupPolicies() {
        RecyclerView rvPolicies = findViewById(R.id.rvPolicies);
        rvPolicies.setLayoutManager(new LinearLayoutManager(this));

        PolicyAdapter adapter = new PolicyAdapter();
        rvPolicies.setAdapter(adapter);

        adapter.submitList(hotelResponse != null ? hotelResponse.getPolicies() : null);
    }

    private void setupBottomCta() {
        b.btnSelectRoom.setOnClickListener(v -> {
            if (hotelResponse == null) return;

            LocalDate today = LocalDate.now();
            String checkinDate = getIntent().getStringExtra(ListRoomTypeActivity.EXTRA_CHECKIN_DATE);
            String checkoutDate = getIntent().getStringExtra(ListRoomTypeActivity.EXTRA_CHECKOUT_DATE);

            if (checkinDate == null || checkinDate.trim().isEmpty()) {
                checkinDate = today.format(apiDateFormatter);
            }
            if (checkoutDate == null || checkoutDate.trim().isEmpty()) {
                checkoutDate = today.plusDays(1).format(apiDateFormatter);
            }

            Intent intent = new Intent(this, ListRoomTypeActivity.class);
            intent.putExtra(ListRoomTypeActivity.EXTRA_HOTEL_ID, hotelResponse.getId());
            intent.putExtra(ListRoomTypeActivity.EXTRA_CHECKIN_DATE, checkinDate);
            intent.putExtra(ListRoomTypeActivity.EXTRA_CHECKOUT_DATE, checkoutDate);
            intent.putExtra(ListRoomTypeActivity.EXTRA_CHECKIN, formatDisplayDate(checkinDate));
            intent.putExtra(ListRoomTypeActivity.EXTRA_CHECKOUT, formatDisplayDate(checkoutDate));
            intent.putExtra(ListRoomTypeActivity.EXTRA_ROOM_QUANTITY,
                    getIntent().getIntExtra(ListRoomTypeActivity.EXTRA_ROOM_QUANTITY, 1));
            intent.putExtra(ListRoomTypeActivity.EXTRA_ADULTS,
                    getIntent().getIntExtra(ListRoomTypeActivity.EXTRA_ADULTS, 2));
            intent.putExtra(ListRoomTypeActivity.EXTRA_CHILDREN,
                    getIntent().getIntExtra(ListRoomTypeActivity.EXTRA_CHILDREN, 0));
            startActivity(intent);
        });
    }

    private String formatDisplayDate(String apiDate) {
        try {
            return LocalDate.parse(apiDate, apiDateFormatter).format(displayDateFormatter);
        } catch (Exception ignore) {
            return apiDate;
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        Log.d(TAG, "onMapReady called");
        if(hotelResponse == null){
            Log.w(TAG, "Hotel data not available, cannot set map location");
            return;
        }
        LatLng location = new LatLng(hotelResponse.getLatitude(),hotelResponse.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f));
        map.addMarker(new MarkerOptions().position(location).title(hotelResponse.getName()));

        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        map.setOnMapLoadedCallback(() -> Log.d(TAG, "Google Map loaded"));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            RoomSelectionStore.clear();
        }
    }
}


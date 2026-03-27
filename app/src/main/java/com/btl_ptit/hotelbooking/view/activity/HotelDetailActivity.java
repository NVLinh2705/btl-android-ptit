package com.btl_ptit.hotelbooking.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.view.adapter.FacilityAdapter;
import com.btl_ptit.hotelbooking.view.adapter.HotelImagePagerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.Arrays;
import java.util.Objects;

public class HotelDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "HotelDetailActivity";

    private GoogleMap googleMap;

    private boolean isFavorite = false;
    private android.graphics.drawable.Drawable favoriteFilledDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Edge-to-edge here can cause the toolbar to be drawn under the status bar
        // unless window insets are handled very carefully. We keep a classic layout
        // and set the status bar color via theme instead.
        setContentView(R.layout.activity_hotel_detail);

        setupToolbar();
        setupImageSlider();
        setupFacilities();
        setupMap();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            // Set title to empty initially, will be set after layout is populated
            getSupportActionBar().setTitle("");
        }

        toolbar.setNavigationOnClickListener(v -> finish());
//        toolbar.inflateMenu(R.menu.menu_hotel_detail);

        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_favorite) {
                toggleFavorite(item);
                return true;
            } else if (id == R.id.action_share) {
                Toast.makeText(this, "Share clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void updateToolbarWithHotelName() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        String hotelName = "";
        try {
            android.widget.TextView tv = findViewById(R.id.txtHotelName);
            if (tv != null) {
                hotelName = tv.getText().toString();
            }
        } catch (Exception e) {
            Log.e(TAG, "Could not find hotel name text view to update toolbar title.", e);
        }

        if (hotelName.trim().isEmpty()) {
            hotelName = getString(R.string.app_name); // Fallback
        }
        toolbar.setTitle(hotelName);
    }

    private void toggleFavorite(android.view.MenuItem item) {
        isFavorite = !isFavorite;

        if (isFavorite) {
            if (favoriteFilledDrawable == null) {
                android.graphics.drawable.Drawable d = ContextCompat.getDrawable(this, R.drawable.ic_heart_filled);
                if (d != null) {
                    d = DrawableCompat.wrap(d.mutate());
                    DrawableCompat.setTint(d, ContextCompat.getColor(this, R.color.color_likedHeartIcon)); // pink
                    Toast.makeText(this, "liked: first change", Toast.LENGTH_SHORT).show();
                    favoriteFilledDrawable = d;
                }
            }

            if (favoriteFilledDrawable != null) {
                item.setIcon(favoriteFilledDrawable);
                Toast.makeText(this, "liked: subsequent change", Toast.LENGTH_SHORT).show();
                // Make sure framework/iconTint from theme doesn't override our drawable tint
                item.setIconTintList(null);
            }
        } else {
            // Back to outline-only (stroke white, fill transparent) so toolbar color is visible inside.
            item.setIcon(R.drawable.ic_heart_outline);
            Toast.makeText(this, "unliked", Toast.LENGTH_SHORT).show();
            item.setIconTintList(null);
        }
    }
//    private void setupMap(Bundle savedInstanceState) {
//        mapView = findViewById(R.id.mapView);
//        if (mapView == null) {
//            Log.w(TAG, "MapView not found in layout");
//            return;
//        }
//
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);
//        Log.d(TAG, "MapView initialized and getMapAsync called");
//    }

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
        ViewPager2 pagerImages = findViewById(R.id.pagerImages);
        ImageButton btnPrev = findViewById(R.id.btnPrev);
        ImageButton btnNext = findViewById(R.id.btnNext);

        HotelImagePagerAdapter adapter = new HotelImagePagerAdapter();
        pagerImages.setAdapter(adapter);

        // Dummy images (replace with your hotel image URLs)
        adapter.submitList(Arrays.asList(
            "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=1200",
            "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=1200",
            "https://cf.bstatic.com/xdata/images/hotel/max1024x768/786400721.jpg?k=1aac9827814b92d51c7ca986cf101c9cdbb3d8f7c18c2a7d7964da32014ed162&o="
        ));

        btnPrev.setOnClickListener(v -> {
            int current = pagerImages.getCurrentItem();
            if (current > 0) pagerImages.setCurrentItem(current - 1, true);
        });

        btnNext.setOnClickListener(v -> {
            int current = pagerImages.getCurrentItem();
            RecyclerView.Adapter<?> a = pagerImages.getAdapter();
            int count = a != null ? a.getItemCount() : 0;
            if (current < count - 1) pagerImages.setCurrentItem(current + 1, true);
        });
    }

    private void setupFacilities() {
        RecyclerView rvFacilities = findViewById(R.id.rvFacilities);
        rvFacilities.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        FacilityAdapter adapter = new FacilityAdapter();
        rvFacilities.setAdapter(adapter);

        adapter.submitList(Arrays.asList(
            new FacilityAdapter.FacilityItem(R.drawable.ic_wifi, "WiFi"),
            new FacilityAdapter.FacilityItem(R.drawable.ic_pool, "Pool"),
            new FacilityAdapter.FacilityItem(R.drawable.ic_wifi, "Gym"),
            new FacilityAdapter.FacilityItem(R.drawable.ic_wifi, "Parking")
        ));

        findViewById(R.id.txtSeeAllFacilities)
            .setOnClickListener(v -> Toast.makeText(this, "See all facilities", Toast.LENGTH_SHORT).show());
        findViewById(R.id.txtSeeAllReviews)
            .setOnClickListener(v -> Toast.makeText(this, "See all reviews", Toast.LENGTH_SHORT).show());
    }
    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        Log.d(TAG, "onMapReady called");

        Toast.makeText(this, "onMapReady called", Toast.LENGTH_SHORT).show();

        LatLng hotel = new LatLng(20.9788183,105.7866833); // example
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(hotel, 14f));
        map.addMarker(new MarkerOptions().position(hotel).title("Hotel"));

        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        map.setOnMapLoadedCallback(() -> Log.d(TAG, "Google Map loaded"));
    }

//    @Override protected void onStart() {
//        super.onStart();
//        if (mapView != null) mapView.onStart();
//    }
//
//    @Override protected void onResume() {
//        super.onResume();
//        if (mapView != null) mapView.onResume();
//    }
//
//    @Override protected void onPause() {
//        if (mapView != null) mapView.onPause();
//        super.onPause();
//    }
//
//    @Override protected void onStop() {
//        if (mapView != null) mapView.onStop();
//        super.onStop();
//    }
//
//    @Override protected void onDestroy() {
//        if (mapView != null) mapView.onDestroy();
//        super.onDestroy();
//    }
//
//    @Override public void onLowMemory() {
//        super.onLowMemory();
//        if (mapView != null) mapView.onLowMemory();
//    }
//
//    @Override protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        if (mapView != null) mapView.onSaveInstanceState(outState);
//    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Set the title here, after the layout has been inflated and views are available.
        updateToolbarWithHotelName();
    }
}
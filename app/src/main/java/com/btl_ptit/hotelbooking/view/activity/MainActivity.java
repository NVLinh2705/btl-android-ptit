package com.btl_ptit.hotelbooking.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.btl_ptit.hotelbooking.databinding.ActivityMainBinding;
import com.btl_ptit.hotelbooking.view.fragment.MyBookingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_POST_NOTIFICATIONS = 2001;

    private ActivityMainBinding mActivityMainBinding;
    private Context mContext;
    private String TAG = "MainActivityTAG";
    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mActivityMainBinding.getRoot());

        mContext = this;

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        requestNotificationPermissionIfNeeded();

        String fragment = getIntent().getStringExtra("OPEN_FRAGMENT");

        if (savedInstanceState == null && "my_booking".equals(fragment)) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_my_booking_fragment);
        }

        // Xử lý back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navController.getCurrentDestination() != null
                        && navController.getCurrentDestination().getId() == R.id.navigation_home_fragment) {
                    finishAffinity();
                } else {
                    navController.navigateUp();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionManager.clearSelectedHotelBrief();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.POST_NOTIFICATIONS },
                REQ_POST_NOTIFICATIONS);
    }
}
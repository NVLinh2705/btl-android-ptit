package com.btl_ptit.hotelbooking.view.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.btl_ptit.hotelbooking.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

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

        // Xử lý back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.navigation_home_fragment) {
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
}
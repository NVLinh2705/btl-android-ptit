package com.btl_ptit.hotelbooking.view.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btl_ptit.hotelbooking.data.model.MyNotification;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.NotificationRestService;
import com.btl_ptit.hotelbooking.databinding.ActivityNotificationBinding;
import com.btl_ptit.hotelbooking.view.adapter.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;
    private NotificationAdapter adapter;
    private NotificationRestService notificationService;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupRecyclerView();
        initApi();
        loadNotifications();
    }

    private void setupToolbar() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter();
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotifications.setAdapter(adapter);
    }

    private void initApi() {
        notificationService = SupabaseClient.createService(NotificationRestService.class);
    }

    private void loadNotifications() {
        if (isLoading)
            return;
        isLoading = true;

        // Fetch last 50 notifications, ordered by most recent first
        compositeDisposable.add(
                notificationService.getMyNotifications(
                        "*", // select all columns
                        "created_at.desc", // order by created_at descending
                        50, // limit
                        0 // offset
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                notifications -> {
                                    isLoading = false;
                                    adapter.submitList(notifications);
                                    if (notifications.isEmpty()) {
                                        binding.rvNotifications.setVisibility(View.GONE);
                                        // Show empty state if needed
                                    } else {
                                        binding.rvNotifications.setVisibility(View.VISIBLE);
                                    }
                                },
                                error -> {
                                    isLoading = false;
                                    error.printStackTrace();
                                    // Show error message or fallback to dummy data
                                    loadDummyData();
                                }));
    }

    private void loadDummyData() {
        // Fallback: show dummy data if API call fails
        List<MyNotification> list = new ArrayList<>();

        MyNotification n1 = new MyNotification();
        n1.setId("1");
        n1.setTitle("Booking Confirmed");
        n1.setContent("Your booking has been confirmed by the hotel.");
        n1.setCreatedAt("2 hours ago");

        MyNotification n2 = new MyNotification();
        n2.setId("2");
        n2.setTitle("Payment Successful");
        n2.setContent("Payment has been successfully processed, your order is being prepared.");
        n2.setCreatedAt("5 hours ago");

        list.add(n1);
        list.add(n2);

        adapter.submitList(list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}

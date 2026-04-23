package com.btl_ptit.hotelbooking.view.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btl_ptit.hotelbooking.data.model.MyNotification;
import com.btl_ptit.hotelbooking.databinding.ActivityNotificationBinding;
import com.btl_ptit.hotelbooking.view.adapter.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupRecyclerView();
        loadDummyData();
    }

    private void setupToolbar() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter();
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotifications.setAdapter(adapter);
    }

    private void loadDummyData() {
        // Sau này bạn sẽ gọi API từ Supabase ở đây
        List<MyNotification> list = new ArrayList<>();
        
        MyNotification n1 = new MyNotification();
        n1.setId("1");
        n1.setTitle("Congratulations, you have successfully booked a room at Jade Gem Resort");
        n1.setCreatedAt("2 hours Ago");
        
        MyNotification n2 = new MyNotification();
        n2.setId("2");
        n2.setTitle("Payment has been successfully made, order is being processed");
        n2.setCreatedAt("5 hours Ago");

        list.add(n1);
        list.add(n2);
        
        adapter.submitList(list);
    }
}

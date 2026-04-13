package com.btl_ptit.hotelbooking.view.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


import com.btl_ptit.hotelbooking.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.view.adapter.RoomTypeAdapter;
import com.btl_ptit.hotelbooking.data.model.RoomType;

import java.util.List;

/**
 * Displays the list of room types for a hotel.
 *
 * Expected Intent extras:
 *   EXTRA_HOTEL_ID    (int)    — the hotel whose rooms to fetch
 *   EXTRA_CHECKIN     (String) — "29 Mar" formatted date for display
 *   EXTRA_CHECKOUT    (String) — "30 Mar"
 */
public class ListRoomTypeActivity extends AppCompatActivity
        implements RoomTypeAdapter.OnRoomSelectedListener {

    public static final String EXTRA_HOTEL_ID  = "hotel_id";
    public static final String EXTRA_CHECKIN   = "checkin";
    public static final String EXTRA_CHECKOUT  = "checkout";

    private RecyclerView            rvRoomTypes;
    private LinearProgressIndicator progressIndicator;
    private View                    layoutEmpty;
    private RoomTypeAdapter         adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_room_type);

        // ── Toolbar ───────────────────────────────────────────────────
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ── Date range subtitle ───────────────────────────────────────
        String checkin  = getIntent().getStringExtra(EXTRA_CHECKIN);
        String checkout = getIntent().getStringExtra(EXTRA_CHECKOUT);
        if (checkin != null && checkout != null) {
            com.google.android.material.textview.MaterialTextView tvDate =
                    findViewById(R.id.tvDateRange);
            tvDate.setText(checkin + " - " + checkout);
        }

        // ── Views ─────────────────────────────────────────────────────
        rvRoomTypes       = findViewById(R.id.rvRoomTypes);
        progressIndicator = findViewById(R.id.progressIndicator);
        layoutEmpty       = findViewById(R.id.layoutEmpty);

        rvRoomTypes.setLayoutManager(new LinearLayoutManager(this));

        // ── Load data ─────────────────────────────────────────────────
        int hotelId = getIntent().getIntExtra(EXTRA_HOTEL_ID, -1);
        fetchRoomTypes(hotelId);
    }

    // ── Data loading ──────────────────────────────────────────────────────

    private void fetchRoomTypes(int hotelId) {
        showLoading(true);

        // TODO: Replace with your actual Retrofit / Supabase API call.
        // Example with a ViewModel + LiveData pattern:
        //
        // viewModel.getRoomTypes(hotelId).observe(this, result -> {
        //     showLoading(false);
        //     if (result.isSuccess()) {
        //         bindRoomTypes(result.getData());
        //     } else {
        //         showError(result.getError());
        //     }
        // });

        // ── Temporary: show empty state ───────────────────────────────
        showLoading(false);
        layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void bindRoomTypes(List<RoomType> rooms) {
        showLoading(false);
        if (rooms == null || rooms.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvRoomTypes.setVisibility(View.GONE);
            return;
        }
        layoutEmpty.setVisibility(View.GONE);
        rvRoomTypes.setVisibility(View.VISIBLE);

        adapter = new RoomTypeAdapter(this, rooms, this);
        rvRoomTypes.setAdapter(adapter);
    }

    // ── RoomTypeAdapter.OnRoomSelectedListener ────────────────────────────

    @Override
    public void onRoomSelected(RoomType roomType) {
        // Open detail screen
        Intent intent = new Intent(this, RoomTypeDetailActivity.class);
        intent.putExtra(RoomTypeDetailActivity.EXTRA_ROOM_TYPE_ID, roomType.getId());
        intent.putExtra(ListRoomTypeActivity.EXTRA_CHECKIN,
                getIntent().getStringExtra(EXTRA_CHECKIN));
        intent.putExtra(ListRoomTypeActivity.EXTRA_CHECKOUT,
                getIntent().getStringExtra(EXTRA_CHECKOUT));
        startActivity(intent);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private void showLoading(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
package com.btl_ptit.hotelbooking.view.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.btl_ptit.hotelbooking.R;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.graphics.Paint;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.flexbox.FlexboxLayout;
import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.view.adapter.RoomImagePagerAdapter;
import com.btl_ptit.hotelbooking.view.adapter.RoomTypeAdapter;
import com.btl_ptit.hotelbooking.data.model.RoomType;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Shows full details for a single room type.
 *
 * Expected Intent extras:
 *   EXTRA_ROOM_TYPE_ID (int)    — room type to display
 *   EXTRA_CHECKIN      (String) — "29 Mar"
 *   EXTRA_CHECKOUT     (String) — "30 Mar"
 */
public class RoomTypeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ROOM_TYPE_ID = "room_type_id";

    private ViewPager2          viewPager;
    private TabLayout           tabDots;
    private MaterialCardView    cardRating;
    private android.widget.TextView tvRatingBadge;
    private android.widget.TextView tvRoomName;
    private android.widget.TextView tvDateRange;
    private android.widget.TextView tvGuests;
    private android.widget.TextView tvCancellation;
    private android.widget.TextView tvPrepayment;
    private View                layoutNoCard;
    private android.widget.TextView tvBookingConditions;
    private android.widget.TextView tvOriginalPrice;
    private android.widget.TextView tvPrice;
    private MaterialButton      btnSelect;
    private View                layoutLastRoom;
    private android.widget.TextView tvLastRoom;
    private View                layoutRoomSize;
    private android.widget.TextView tvRoomSize;
    private FlexboxLayout       flexFacilities;

    private final NumberFormat currencyFormat =
            NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_type_detail);

        bindViews();
        setupToolbar();

        // Date range subtitle
        String checkin  = getIntent().getStringExtra(ListRoomTypeActivity.EXTRA_CHECKIN);
        String checkout = getIntent().getStringExtra(ListRoomTypeActivity.EXTRA_CHECKOUT);
        if (checkin != null && checkout != null) {
            tvDateRange.setText(checkin + " - " + checkout);
            tvDateRange.setVisibility(View.VISIBLE);
        }

        int roomTypeId = getIntent().getIntExtra(EXTRA_ROOM_TYPE_ID, -1);
        fetchRoomType(roomTypeId);
    }

    // ── View binding ──────────────────────────────────────────────────────

    private void bindViews() {
        viewPager            = findViewById(R.id.viewPagerImages);
        tabDots              = findViewById(R.id.tabDots);
        cardRating           = findViewById(R.id.cardRating);
        tvRatingBadge        = findViewById(R.id.tvRatingBadge);
        tvRoomName           = findViewById(R.id.tvRoomName);
        tvDateRange          = findViewById(R.id.tvDateRange);
        tvGuests             = findViewById(R.id.tvGuests);
        tvCancellation       = findViewById(R.id.tvCancellation);
        tvPrepayment         = findViewById(R.id.tvPrepayment);
        layoutNoCard         = findViewById(R.id.layoutNoCard);
        tvBookingConditions  = findViewById(R.id.tvBookingConditions);
        tvOriginalPrice      = findViewById(R.id.tvOriginalPrice);
        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvPrice              = findViewById(R.id.tvPrice);
        btnSelect            = findViewById(R.id.btnSelect);
        layoutLastRoom       = findViewById(R.id.layoutLastRoom);
        tvLastRoom           = findViewById(R.id.tvLastRoom);
        layoutRoomSize       = findViewById(R.id.layoutRoomSize);
        tvRoomSize           = findViewById(R.id.tvRoomSize);
        flexFacilities       = findViewById(R.id.flexFacilities);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(v -> finish());

        // Make toolbar icons white over photo, switch to dark when collapsed
        AppBarLayout appBar = findViewById(R.id.appBarLayout);
        appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int totalScrollRange = appBarLayout.getTotalScrollRange();
            float fraction = Math.abs(verticalOffset) / (float) totalScrollRange;
            // When fully collapsed (fraction ≈ 1), switch icon tints to dark
            int iconColor = fraction > 0.7f
                    ? getColor(android.R.color.black)
                    : getColor(android.R.color.white);
            toolbar.setNavigationIconTint(iconColor);
        });
    }

    // ── Data loading ──────────────────────────────────────────────────────

    private void fetchRoomType(int id) {
        // TODO: Replace with your actual ViewModel / repository call.
        // Example:
        // viewModel.getRoomType(id).observe(this, result -> {
        //     if (result.isSuccess()) bindRoomType(result.getData());
        // });
    }

    /** Bind a RoomType object to all views. Call this once data arrives. */
    public void bindRoomType(RoomType room) {
        // ── Photo gallery ─────────────────────────────────────────────
        List<String> images = room.getImageUrls();
        if (images != null && !images.isEmpty()) {
            RoomImagePagerAdapter pagerAdapter =
                    new RoomImagePagerAdapter(this, images);
            viewPager.setAdapter(pagerAdapter);
            new TabLayoutMediator(tabDots, viewPager, (tab, pos) -> {}).attach();
            tabDots.setVisibility(images.size() > 1 ? View.VISIBLE : View.GONE);
        }

        // ── Rating badge (optional) ───────────────────────────────────
        // Show if you have a rating from the API:
        // cardRating.setVisibility(View.VISIBLE);
        // tvRatingBadge.setText("8.8 Super comfy");

        // ── Name ─────────────────────────────────────────────────────
        tvRoomName.setText(room.getName());

        // ── Guests ───────────────────────────────────────────────────
        tvGuests.setText(getString(R.string.price_for_n_adults, room.getMaxGuests()));

        // ── Cancellation ─────────────────────────────────────────────
        if (room.isHasFreeCancellation()) {
            tvCancellation.setText(R.string.free_cancellation);
            tvCancellation.setTextColor(getColor(R.color.green_700));
        } else {
            tvCancellation.setText(R.string.total_cost_to_cancel);
        }

        // ── No prepayment (bold + normal mixed text) ──────────────────
        String bold = getString(R.string.no_prepayment_needed);
        String rest = getString(R.string.pay_at_property);
        SpannableString ss = new SpannableString(bold + rest);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, bold.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPrepayment.setText(ss);

        // ── Price ─────────────────────────────────────────────────────
        String price = "VND " + currencyFormat.format((long) room.getBasePricePerNight());
        tvPrice.setText(price);
        tvOriginalPrice.setVisibility(View.GONE); // set if discounted

        // ── Last room ─────────────────────────────────────────────────
        if (room.getQuantity() > 0 && room.getQuantity() <= 3) {
            layoutLastRoom.setVisibility(View.VISIBLE);
            tvLastRoom.setText(room.getQuantity() == 1
                    ? getString(R.string.we_have_1_left)
                    : getString(R.string.we_have_n_left, room.getQuantity()));
        }

        // ── Room size ─────────────────────────────────────────────────
        if (room.getArea() != null) {
            layoutRoomSize.setVisibility(View.VISIBLE);
            tvRoomSize.setText("Room size: " + room.getArea().intValue() + " m\u00B2");
        }

        // ── Facilities ────────────────────────────────────────────────
        RoomTypeAdapter adapterHelper = new RoomTypeAdapter(this, new ArrayList<>(), null);
        adapterHelper.populateFacilities(flexFacilities, room.getFacilities(), Integer.MAX_VALUE);

        // ── Select button ─────────────────────────────────────────────
        btnSelect.setOnClickListener(v -> {
            // TODO: navigate to booking confirmation screen
//            Intent intent = new Intent(this, BookingActivity.class);
//            intent.putExtra("room_type_id", room.getId());
//            startActivity(intent);
        });

        // ── Booking conditions link ───────────────────────────────────
        tvBookingConditions.setOnClickListener(v -> {
            // TODO: show bottom sheet or dialog with full conditions
        });
    }
}
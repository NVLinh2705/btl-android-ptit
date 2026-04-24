package com.btl_ptit.hotelbooking.view.activity;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.content.res.ColorStateList;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.dto.HotelReview;
import com.btl_ptit.hotelbooking.data.dto.Policy;
import com.btl_ptit.hotelbooking.data.dto.RoomTypeDetailResponse;
import com.btl_ptit.hotelbooking.data.model.RoomType;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.SupabaseRestService;
import com.btl_ptit.hotelbooking.data.session.RoomSelectionStore;
import com.btl_ptit.hotelbooking.utils.CurrencyUtils;
import com.btl_ptit.hotelbooking.view.adapter.FacilitiesGroupedAdapter;
import com.btl_ptit.hotelbooking.view.adapter.HotelReviewsAdapter;
import com.btl_ptit.hotelbooking.view.adapter.RoomImagePagerAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomTypeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ROOM_TYPE_ID = "room_type_id";

    private ViewPager2 viewPager;
    private LinearLayout layoutImageDots;
    private androidx.core.widget.NestedScrollView scrollContent;
    private TextView tvRoomName;
    private TextView tvGuests;
    private TextView tvCancellation;
    private TextView tvPrepayment;
    private TextView tvPrice;
    private TextView tvRoomSize;
    private TextView tvDescription;
    private TextView tvBedSummary;
    private View layoutArea;

    private LinearLayout layoutSelectionCta;
    private TextView tvSelectionSummary;
    private TextView tvSelectionPrice;
    private MaterialButton btnSelect;
    private ImageButton btnRemoveSelection;

    private SupabaseRestService restService;
    private RoomType currentRoomType;

    private String checkinDisplay;
    private String checkoutDisplay;
    private String checkinApi;
    private String checkoutApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_type_detail);

        restService = SupabaseClient.createService(SupabaseRestService.class);
        bindViews();
        initDateExtras();
        setupToolbar();
        setupStaticSections();

        int roomTypeId = getIntent().getIntExtra(EXTRA_ROOM_TYPE_ID, -1);
        fetchRoomType(roomTypeId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSelectionCta();
        updateSelectButtonState();
    }

    private void bindViews() {
        scrollContent = findViewById(R.id.scrollContent);
        viewPager = findViewById(R.id.viewPagerImages);
        layoutImageDots = findViewById(R.id.layoutImageDots);
        tvRoomName = findViewById(R.id.tvRoomName);
        tvGuests = findViewById(R.id.tvGuests);
        tvCancellation = findViewById(R.id.tvCancellation);
        tvPrepayment = findViewById(R.id.tvPrepayment);
        tvPrice = findViewById(R.id.tvPrice);
        tvRoomSize = findViewById(R.id.tvRoomSize);
        tvDescription = findViewById(R.id.tvDescription);
        tvBedSummary = findViewById(R.id.tvBedSummary);
        layoutArea = findViewById(R.id.layoutArea);

        layoutSelectionCta = findViewById(R.id.layoutSelectionCta);
        tvSelectionSummary = findViewById(R.id.tvSelectionSummary);
        tvSelectionPrice = findViewById(R.id.tvSelectionPrice);
        btnSelect = findViewById(R.id.btnSelect);
        btnRemoveSelection = findViewById(R.id.btnRemoveSelection);
    }

    private void initDateExtras() {
        checkinDisplay = getIntent().getStringExtra(ListRoomTypeActivity.EXTRA_CHECKIN);
        checkoutDisplay = getIntent().getStringExtra(ListRoomTypeActivity.EXTRA_CHECKOUT);
        checkinApi = getIntent().getStringExtra(ListRoomTypeActivity.EXTRA_CHECKIN_DATE);
        checkoutApi = getIntent().getStringExtra(ListRoomTypeActivity.EXTRA_CHECKOUT_DATE);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        getWindow().setStatusBarColor(getColor(R.color.toolbar_blue));
    }

    private void setupStaticSections() {
        findViewById(R.id.btnBookNow).setOnClickListener(v -> {
            if (!RoomSelectionStore.hasSelection()) {
                Toast.makeText(this, "Vui lòng chọn phòng trước khi tiếp tục", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new android.content.Intent(this, FillBookingInfoActivity.class));
        });

        btnSelect.setOnClickListener(v -> {
            if (currentRoomType == null) return;
            showQuantityPicker(currentRoomType);
        });

        btnRemoveSelection.setOnClickListener(v -> {
            if (currentRoomType == null) return;
            RoomSelectionStore.removeRoom(currentRoomType.getId());
            updateSelectionCta();
            updateSelectButtonState();
        });
    }

    private void fetchRoomType(int roomTypeId) {
        showLoading(true);
        if (roomTypeId <= 0) {
            showLoading(false);
            Toast.makeText(this, R.string.error_load_room_types, Toast.LENGTH_SHORT).show();
            return;
        }

        restService.getRoomTypeDetail(roomTypeId).enqueue(new Callback<RoomTypeDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<RoomTypeDetailResponse> call,
                                   @NonNull Response<RoomTypeDetailResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    showLoading(false);
                    Toast.makeText(RoomTypeDetailActivity.this, R.string.error_load_room_types, Toast.LENGTH_SHORT).show();
                    return;
                }
                bindRoomType(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<RoomTypeDetailResponse> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(RoomTypeDetailActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindRoomType(RoomTypeDetailResponse data) {
        RoomType room = mapRoomType(data);
        currentRoomType = room;
        RoomSelectionStore.attachHotel(room.getHotelId());

        tvRoomName.setText(room.getName());
        setupToolbarTitle(room.getName(), buildDateRangeLabel());

        int nights = calculateNights(checkinApi, checkoutApi);
        tvGuests.setText(getString(R.string.price_for_n_adults, room.getMaxGuests()));

        String cancellationPolicy = findPolicyContent(data.getPolicies(), "CANCELLATION");
        String paymentPolicy = findPolicyContent(data.getPolicies(), "PAYMENT");
        tvCancellation.setText(cancellationPolicy != null ? cancellationPolicy : getString(R.string.total_cost_to_cancel));
        tvPrepayment.setText(paymentPolicy != null ? paymentPolicy : getString(R.string.no_prepayment_needed));

        double totalPrice = room.getBasePricePerNight() * Math.max(1, nights);
        room.setTotalPrice(totalPrice);
        room.setNights(Math.max(1, nights));
        tvPrice.setText(getString(R.string.price_for_n_nights, room.getNights(), CurrencyUtils.formatVnd(totalPrice)));

        if (room.getArea() != null) {
            layoutArea.setVisibility(View.VISIBLE);
            tvRoomSize.setText(getString(R.string.room_size_label, room.getArea().intValue()));
        } else {
            layoutArea.setVisibility(View.GONE);
        }

        tvDescription.setText(data.getDescription() != null ? data.getDescription() : "");
        tvBedSummary.setText(room.getBedSummary());

        setupImageSlider(extractImageUrls(data.getImages()));
        setupFacilities(data);
//        setupReviews(data.getReviews());

        updateSelectionCta();
        updateSelectButtonState();
        showLoading(false);
    }

    private void setupToolbarTitle(String roomName, String dateLabel) {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(roomName);

        if (dateLabel == null || dateLabel.trim().isEmpty()) {
            toolbar.setSubtitle(null);
            return;
        }

        SpannableString ss = new SpannableString(dateLabel);
        ss.setSpan(new RelativeSizeSpan(0.85f), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        toolbar.setSubtitle(ss);
    }

    private String buildDateRangeLabel() {
        if (checkinDisplay != null && checkoutDisplay != null) {
            return checkinDisplay + " - " + checkoutDisplay;
        }
        if (checkinApi != null && checkoutApi != null) {
            return checkinApi + " - " + checkoutApi;
        }
        return "";
    }

    private int calculateNights(String checkin, String checkout) {
        if (checkin == null || checkout == null) return 1;
        try {
            LocalDate in = LocalDate.parse(checkin);
            LocalDate out = LocalDate.parse(checkout);
            long nights = ChronoUnit.DAYS.between(in, out);
            return (int) Math.max(1, nights);
        } catch (Exception ignored) {
            return 1;
        }
    }

    private void setupImageSlider(List<String> imageUrls) {
        RoomImagePagerAdapter pagerAdapter = new RoomImagePagerAdapter(this, imageUrls);
        viewPager.setAdapter(pagerAdapter);

        int dotCount = Math.min(6, imageUrls.size());
        setupImageDots(dotCount);
        updateImageDots(0, imageUrls.size(), dotCount);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateImageDots(position, imageUrls.size(), dotCount);
            }
        });
    }

    private void setupImageDots(int dotCount) {
        layoutImageDots.removeAllViews();
        for (int i = 0; i < dotCount; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpToPx(6), dpToPx(6));
            lp.setMargins(dpToPx(3), 0, dpToPx(3), 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(R.drawable.tab_indicator_dot);
            layoutImageDots.addView(dot);
        }
        layoutImageDots.setVisibility(dotCount > 1 ? View.VISIBLE : View.GONE);
    }

    private void updateImageDots(int position, int imageCount, int dotCount) {
        if (dotCount <= 0) return;
        int activeIndex = imageCount <= dotCount
                ? position
                : Math.min(dotCount - 1, position * dotCount / imageCount);
        for (int i = 0; i < layoutImageDots.getChildCount(); i++) {
            layoutImageDots.getChildAt(i).setSelected(i == activeIndex);
        }
    }

    private void setupFacilities(RoomTypeDetailResponse data) {
        androidx.recyclerview.widget.RecyclerView rvFacilities = findViewById(R.id.rvFacilitiesGrouped);
        rvFacilities.setLayoutManager(new LinearLayoutManager(this));

        FacilitiesGroupedAdapter adapter = new FacilitiesGroupedAdapter();
        rvFacilities.setAdapter(adapter);
        adapter.submitList(data.getFacilitiesFlat());
    }

//    private void setupReviews(List<HotelReview> reviews) {
//        androidx.recyclerview.widget.RecyclerView rvReviews = findViewById(R.id.rvReviews);
//        TextView tvNoReviews = findViewById(R.id.tvNoReviews);
//        rvReviews.setLayoutManager(new LinearLayoutManager(this));
//
//        HotelReviewsAdapter adapter = new HotelReviewsAdapter(roomTypeInfo -> {
//            // no-op on room-type card click in this screen
//        });
//        rvReviews.setAdapter(adapter);
//        if (reviews == null || reviews.isEmpty()) {
//            adapter.submitList(new ArrayList<>());
//            rvReviews.setVisibility(View.GONE);
//            tvNoReviews.setVisibility(View.VISIBLE);
//        } else {
//            adapter.submitList(reviews);
//            rvReviews.setVisibility(View.VISIBLE);
//            tvNoReviews.setVisibility(View.GONE);
//        }
//    }

    private List<String> extractImageUrls(List<RoomTypeDetailResponse.ImageItem> images) {
        List<String> urls = new ArrayList<>();
        if (images != null) {
            for (RoomTypeDetailResponse.ImageItem image : images) {
                if (image != null && image.getUrl() != null && !image.getUrl().trim().isEmpty()) {
                    urls.add(image.getUrl());
                }
            }
        }
        if (urls.isEmpty()) {
            urls.add("");
        }
        return urls;
    }

    private String findPolicyContent(List<Policy> policies, String typeCode) {
        if (policies == null) return null;
        for (Policy p : policies) {
            if (p == null || p.getTypeCode() == null) continue;
            if (typeCode.equalsIgnoreCase(p.getTypeCode())) {
                return p.getContent();
            }
        }
        return null;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private RoomType mapRoomType(RoomTypeDetailResponse item) {
        RoomType roomType = new RoomType();
        roomType.setId(item.getId());
        roomType.setHotelId(item.getHotelId());
        roomType.setName(item.getName());
        roomType.setDescription(item.getDescription());
        roomType.setMaxGuests(item.getMaxGuests());
        roomType.setBedCount(item.getBedCount());
        roomType.setBedType(item.getBedType());
        roomType.setBasePricePerNight(item.getBasePricePerNight());
        roomType.setHasFreeCancellation(item.isHasFreeCancellation());
        roomType.setQuantity(item.getQuantity());
        roomType.setArea(item.getArea() != null ? item.getArea().doubleValue() : null);
        return roomType;
    }

    private void updateSelectionCta() {
        if (!RoomSelectionStore.hasSelection()) {
            layoutSelectionCta.setVisibility(View.GONE);
            return;
        }

        layoutSelectionCta.setVisibility(View.VISIBLE);
        int rooms = RoomSelectionStore.getSelectedRoomCount();
        int beds = RoomSelectionStore.getSelectedBedCount();
        double totalPrice = RoomSelectionStore.getTotalPrice();

        tvSelectionSummary.setText(getString(R.string.selected_room_bed_summary, rooms, beds));
        tvSelectionPrice.setText(CurrencyUtils.formatVnd(totalPrice));
    }

    private void updateSelectButtonState() {
        if (currentRoomType == null || btnSelect == null || btnRemoveSelection == null) {
            return;
        }

        int selectedCount = RoomSelectionStore.getRoomCount(currentRoomType.getId());
        boolean isSelected = selectedCount > 0;

        if (isSelected) {
            btnSelect.setText(getString(R.string.selected_room_button, selectedCount));
            btnSelect.setIconResource(R.drawable.ic_arrow_down);
            btnSelect.setIconGravity(MaterialButton.ICON_GRAVITY_END);
            btnSelect.setIconPadding(8);
            btnSelect.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.toolbar_blue)));
            btnSelect.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white)));
            btnSelect.setTextColor(ContextCompat.getColor(this, R.color.toolbar_blue));
            btnSelect.setStrokeWidth(2);
            btnSelect.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.toolbar_blue)));
            btnRemoveSelection.setVisibility(View.VISIBLE);
            return;
        }

        btnSelect.setText(R.string.select);
        btnSelect.setIcon(null);
        btnSelect.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.toolbar_blue)));
        btnSelect.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        btnSelect.setStrokeWidth(0);
        btnRemoveSelection.setVisibility(View.GONE);
    }

    private void showLoading(boolean show) {
        View shimmerLoading = findViewById(R.id.shimmerLoading);
        if (!(shimmerLoading instanceof com.facebook.shimmer.ShimmerFrameLayout)) {
            return;
        }
        com.facebook.shimmer.ShimmerFrameLayout shimmer = (com.facebook.shimmer.ShimmerFrameLayout) shimmerLoading;

        if (show) {
            shimmer.setVisibility(View.VISIBLE);
            shimmer.startShimmer();
            if (scrollContent != null) {
                scrollContent.setVisibility(View.GONE);
            }
            layoutSelectionCta.setVisibility(View.GONE);
            return;
        }

        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
        if (scrollContent != null) {
            scrollContent.setVisibility(View.VISIBLE);
        }
    }

    private void showQuantityPicker(RoomType roomType) {
        if (roomType == null) return;
        int maxAvailable = Math.max(1, roomType.getQuantity());
        int current = RoomSelectionStore.getRoomCount(roomType.getId());
        final int[] quantity = {Math.max(1, Math.min(current > 0 ? current : 1, maxAvailable))};

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View content = getLayoutInflater().inflate(R.layout.dialog_room_quantity_picker, null, false);
        dialog.setContentView(content);

        TextView tvTitle = content.findViewById(R.id.tvPickerTitle);
        TextView tvAvailable = content.findViewById(R.id.tvPickerAvailable);
        TextView tvQuantity = content.findViewById(R.id.tvQuantity);
        View btnMinus = content.findViewById(R.id.btnMinus);
        View btnPlus = content.findViewById(R.id.btnPlus);
        View btnConfirm = content.findViewById(R.id.btnConfirm);

        tvTitle.setText(getString(R.string.choose_room_quantity));
        tvAvailable.setText(getString(R.string.room_quantity_available, maxAvailable));
        tvQuantity.setText(String.valueOf(quantity[0]));

        btnMinus.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0] -= 1;
                tvQuantity.setText(String.valueOf(quantity[0]));
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (quantity[0] < maxAvailable) {
                quantity[0] += 1;
                tvQuantity.setText(String.valueOf(quantity[0]));
            }
        });

        btnConfirm.setOnClickListener(v -> {
            RoomSelectionStore.setRoomCount(roomType, quantity[0]);
            updateSelectionCta();
            updateSelectButtonState();
            Toast.makeText(this, "Đã cập nhật lựa chọn", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }
}
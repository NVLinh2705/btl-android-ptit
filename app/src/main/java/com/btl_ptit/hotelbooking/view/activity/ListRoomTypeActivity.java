package com.btl_ptit.hotelbooking.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.dto.AvailableRoomTypesResponse;
import com.btl_ptit.hotelbooking.data.dto.HotelFacility;
import com.btl_ptit.hotelbooking.data.model.Facility;
import com.btl_ptit.hotelbooking.data.model.RoomType;
import com.btl_ptit.hotelbooking.data.session.RoomSelectionStore;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.SupabaseRestService;
import com.btl_ptit.hotelbooking.databinding.ActivityListRoomTypeBinding;
import com.btl_ptit.hotelbooking.view.adapter.RoomTypeAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Displays the list of room types for a hotel.
 *
 * Expected Intent extras:
 *   EXTRA_HOTEL_ID      (int)    - hotel id
 *   EXTRA_CHECKIN_DATE  (String) - YYYY-MM-DD
 *   EXTRA_CHECKOUT_DATE (String) - YYYY-MM-DD
 *   EXTRA_ROOM_QUANTITY (int)    - requested room count
 *   EXTRA_ADULTS        (int)
 *   EXTRA_CHILDREN      (int)
 */
public class ListRoomTypeActivity extends AppCompatActivity
        implements RoomTypeAdapter.OnRoomActionListener {

    public static final String EXTRA_HOTEL_ID  = "hotel_id";
    public static final String EXTRA_CHECKIN   = "checkin";
    public static final String EXTRA_CHECKOUT  = "checkout";
    public static final String EXTRA_CHECKIN_DATE = "checkin_date";
    public static final String EXTRA_CHECKOUT_DATE = "checkout_date";
    public static final String EXTRA_ROOM_QUANTITY = "room_quantity";
    public static final String EXTRA_ADULTS = "adults";
    public static final String EXTRA_CHILDREN = "children";

    private final DateTimeFormatter apiDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private final DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ActivityListRoomTypeBinding b;
    private RoomTypeAdapter adapter;
    private SupabaseRestService restService;
    private final NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    private String checkinApi;
    private String checkoutApi;
    private int roomQuantity;
    private int adults;
    private int children;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityListRoomTypeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        restService = SupabaseClient.createService(SupabaseRestService.class);
        RoomSelectionStore.attachHotel(getIntent().getIntExtra(EXTRA_HOTEL_ID, -1));

        setSupportActionBar(b.toolbar);
        b.toolbar.setNavigationOnClickListener(v -> finish());
        b.btnBookNow.setOnClickListener(v -> Toast.makeText(this, "Tiếp tục đặt phòng", Toast.LENGTH_SHORT).show());

        getWindow().setStatusBarColor(getColor(R.color.toolbar_blue));

        initSearchParams();
        setToolbarSubtitle(buildDateRangeLabel());


        b.rvRoomTypes.setLayoutManager(new LinearLayoutManager(this));

        int hotelId = getIntent().getIntExtra(EXTRA_HOTEL_ID, -1);
        fetchRoomTypes(hotelId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSelectionCta();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void fetchRoomTypes(int hotelId) {
        if (hotelId <= 0) {
            showLoading(false);
            b.layoutEmpty.setVisibility(View.VISIBLE);
            return;
        }

        showLoading(true);

        restService.getAvailableRoomTypes(
                hotelId,
                checkinApi,
                checkoutApi,
                roomQuantity,
                adults,
                children
        ).enqueue(new Callback<AvailableRoomTypesResponse>() {
            @Override
            public void onResponse(Call<AvailableRoomTypesResponse> call, Response<AvailableRoomTypesResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    showLoading(false);
                    b.layoutEmpty.setVisibility(View.VISIBLE);
                    b.rvRoomTypes.setVisibility(View.GONE);
                    Toast.makeText(ListRoomTypeActivity.this, R.string.error_load_room_types, Toast.LENGTH_SHORT).show();
                    return;
                }
                bindRoomTypes(mapRoomTypes(response.body()));
            }

            @Override
            public void onFailure(Call<AvailableRoomTypesResponse> call, Throwable t) {
                showLoading(false);
                b.layoutEmpty.setVisibility(View.VISIBLE);
                b.rvRoomTypes.setVisibility(View.GONE);
                Toast.makeText(ListRoomTypeActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindRoomTypes(List<RoomType> rooms) {
        showLoading(false);
        if (rooms == null || rooms.isEmpty()) {
            b.layoutEmpty.setVisibility(View.VISIBLE);
            b.rvRoomTypes.setVisibility(View.GONE);
            return;
        }

        b.layoutEmpty.setVisibility(View.GONE);
        b.rvRoomTypes.setVisibility(View.VISIBLE);

        adapter = new RoomTypeAdapter(this, rooms, this);
        b.rvRoomTypes.setAdapter(adapter);
        updateSelectionCta();
    }

    @Override
    public void onRoomClicked(RoomType roomType) {
        Intent intent = new Intent(this, RoomTypeDetailActivity.class);
        intent.putExtra(RoomTypeDetailActivity.EXTRA_ROOM_TYPE_ID, roomType.getId());
        intent.putExtra(EXTRA_CHECKIN, buildDisplayDate(checkinApi));
        intent.putExtra(EXTRA_CHECKOUT, buildDisplayDate(checkoutApi));
        intent.putExtra(EXTRA_CHECKIN_DATE, checkinApi);
        intent.putExtra(EXTRA_CHECKOUT_DATE, checkoutApi);
        intent.putExtra(EXTRA_HOTEL_ID, roomType.getHotelId());
        startActivity(intent);
    }

    @Override
    public void onSelectRoom(RoomType roomType) {
        showQuantityPicker(roomType);
    }

    @Override
    public void onRemoveRoom(RoomType roomType) {
        RoomSelectionStore.removeRoom(roomType.getId());
        if (adapter != null) adapter.notifyDataSetChanged();
        updateSelectionCta();
    }

    private void showLoading(boolean show) {
        b.progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void initSearchParams() {
        LocalDate today = LocalDate.now();

        String checkinFromIntent = getIntent().getStringExtra(EXTRA_CHECKIN_DATE);
        String checkoutFromIntent = getIntent().getStringExtra(EXTRA_CHECKOUT_DATE);

        checkinApi = !TextUtils.isEmpty(checkinFromIntent)
                ? checkinFromIntent
                : today.format(apiDateFormatter);
        checkoutApi = !TextUtils.isEmpty(checkoutFromIntent)
                ? checkoutFromIntent
                : today.plusDays(1).format(apiDateFormatter);

        roomQuantity = getIntent().getIntExtra(EXTRA_ROOM_QUANTITY, 1);
        adults = getIntent().getIntExtra(EXTRA_ADULTS, 2);
        children = getIntent().getIntExtra(EXTRA_CHILDREN, 0);

        if (roomQuantity <= 0) roomQuantity = 1;
        if (adults <= 0) adults = 1;
        if (children < 0) children = 0;
    }

    private String buildDateRangeLabel() {
        String checkinDisplay = getIntent().getStringExtra(EXTRA_CHECKIN);
        String checkoutDisplay = getIntent().getStringExtra(EXTRA_CHECKOUT);

        if (!TextUtils.isEmpty(checkinDisplay) && !TextUtils.isEmpty(checkoutDisplay)) {
            return checkinDisplay + " - " + checkoutDisplay;
        }
        return buildDisplayDate(checkinApi) + " - " + buildDisplayDate(checkoutApi);
    }

    private String buildDisplayDate(String apiDate) {
        try {
            return LocalDate.parse(apiDate, apiDateFormatter).format(displayDateFormatter);
        } catch (Exception ignore) {
            return apiDate;
        }
    }

    private void setToolbarSubtitle(String subtitle) {
        SpannableString ss = new SpannableString(subtitle);
        ss.setSpan(new RelativeSizeSpan(0.85f), 0, ss.length(), 0);
        b.toolbar.setSubtitle(ss);
    }

    private void updateSelectionCta() {
        if (!RoomSelectionStore.hasSelection()) {
            b.layoutSelectionCta.setVisibility(View.GONE);
            return;
        }

        b.layoutSelectionCta.setVisibility(View.VISIBLE);
        int rooms = RoomSelectionStore.getSelectedRoomCount();
        int beds = RoomSelectionStore.getSelectedBedCount();
        double totalPrice = RoomSelectionStore.getTotalPrice();

        b.tvSelectionSummary.setText(getString(R.string.selected_room_bed_summary, rooms, beds));
        b.tvSelectionPrice.setText("VND " + currencyFormat.format((long) totalPrice));
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
            if (adapter != null) adapter.notifyDataSetChanged();
            updateSelectionCta();
            dialog.dismiss();
        });

        dialog.show();
    }

    private List<RoomType> mapRoomTypes(AvailableRoomTypesResponse response) {
        if (response.getData() == null || response.getData().isEmpty()) {
            return Collections.emptyList();
        }

        List<RoomType> mapped = new ArrayList<>();
        for (AvailableRoomTypesResponse.AvailableRoomTypeItem item : response.getData()) {
            RoomType roomType = new RoomType();
            roomType.setId(item.getId());
            roomType.setHotelId(item.getHotelId());
            roomType.setName(item.getName());
            roomType.setMaxGuests(item.getMaxGuests());
            roomType.setBedCount(item.getBedCount());
            roomType.setBedType(item.getBedType());
            roomType.setArea(item.getArea());
            roomType.setHasFreeCancellation(item.isHasFreeCancellation());
            roomType.setBasePricePerNight(item.getBasePricePerNight());
            roomType.setTotalPrice(item.getTotalPrice());
            roomType.setNights(item.getNights());
            roomType.setQuantity(item.getAvailableQuantity());

            if (!TextUtils.isEmpty(item.getThumbnailUrl())) {
                roomType.setImageUrls(Collections.singletonList(item.getThumbnailUrl()));
            } else {
                roomType.setImageUrls(Collections.emptyList());
            }

            roomType.setFacilities(mapFacilities(item.getFacilities()));
            roomType.setCancellationPolicy(getPolicyContent(item.getPolicies(), "CANCELLATION"));
            roomType.setPaymentPolicy(getPolicyContent(item.getPolicies(), "PAYMENT"));
            mapped.add(roomType);
        }

        return mapped;
    }

    private List<Facility> mapFacilities(List<HotelFacility> facilities) {
        if (facilities == null || facilities.isEmpty()) return Collections.emptyList();

        List<Facility> mapped = new ArrayList<>();
        for (HotelFacility facility : facilities) {
            Facility item = new Facility();
            item.setId(facility.getId());
            item.setName(facility.getName());
            item.setNameVi(facility.getNameVi());
            item.setFacilityTypeId(facility.getTypeId());
            mapped.add(item);
        }
        return mapped;
    }

    private String getPolicyContent(List<AvailableRoomTypesResponse.RoomPolicy> policies, String typeCode) {
        if (policies == null || policies.isEmpty()) return null;

        for (AvailableRoomTypesResponse.RoomPolicy policy : policies) {
            if (policy == null || policy.getTypeCode() == null) continue;
            if (typeCode.equalsIgnoreCase(policy.getTypeCode()) && !TextUtils.isEmpty(policy.getContent())) {
                return policy.getContent();
            }
        }
        return null;
    }
}
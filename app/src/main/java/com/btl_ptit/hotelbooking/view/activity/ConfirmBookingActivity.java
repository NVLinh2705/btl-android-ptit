package com.btl_ptit.hotelbooking.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.dto.CreateBookingRequest;
import com.btl_ptit.hotelbooking.data.dto.CreateBookingResponse;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.BookingRestService;
import com.btl_ptit.hotelbooking.data.session.RoomSelectionStore;
import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.btl_ptit.hotelbooking.databinding.ActivityConfirmBookingBinding;
import com.btl_ptit.hotelbooking.utils.CurrencyUtils;
import com.btl_ptit.hotelbooking.view.adapter.ConfirmRoomChoiceAdapter;
import com.google.android.material.button.MaterialButton;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmBookingActivity extends AppCompatActivity {

    private ActivityConfirmBookingBinding b;
    private final SessionManager sessionManager = SessionManager.getInstance();

    private String checkinDate;
    private String checkoutDate;
    private int numAdults, numChildren,nights;
    private BookingRestService service;

    private SessionManager.SelectedHotelBrief selectedHotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityConfirmBookingBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        parseSessionData();
        setupToolbar();
        setupSelectedRooms();
        bindGuestInfo();
        bindDateSummary();
        bindTotalPrice();
        bindCachedHotelInfo();


        b.btnConfirmBooking.setOnClickListener(v ->
               placeBooking()
        );
    }

    private void placeBooking() {
        // 1. Show Loading Overlay & Disable Button
        b.loadingOverlay.setVisibility(View.VISIBLE);
        b.btnConfirmBooking.setEnabled(false);

        // 2. Build your request object (Fill with actual data from your Intent/Session)
        CreateBookingRequest request = new CreateBookingRequest();
         request.setHotelId(selectedHotel.getHotelId());
         request.setCheckInDate(checkinDate);
         request.setCheckOutDate(checkoutDate);
         request.setNumAdults(numAdults);
         request.setNumChildren(numChildren);
         request.setGuestEmail(b.tvGuestEmailValue.getText().toString());
         request.setGuestName(b.tvGuestFullNameValue.getText().toString());
         request.setGuestPhone(b.tvGuestPhoneValue.getText().toString());
         request.setSpecialRequests(b.etSpecialRequest.getText() == null? "" : b.etSpecialRequest.getText().toString());
         request.setRoomSelections(RoomSelectionStore.getSelectionItems().stream().map(item -> {
             CreateBookingRequest.RoomSelection selection = new CreateBookingRequest.RoomSelection();
             selection.setRoomTypeId(item.getRoomType().getId());
             selection.setQuantity(item.getCount());
             selection.setPriceAtBooking(item.getUnitPrice());
             return selection;
         }).toList());


        service = SupabaseClient.createService(BookingRestService.class);
        service.createBooking(request).enqueue(new Callback<CreateBookingResponse>() {
            @Override
            public void onResponse(Call<CreateBookingResponse> call, Response<CreateBookingResponse> response) {
                b.loadingOverlay.setVisibility(View.GONE);
                b.btnConfirmBooking.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    showSuccessDialog(response.body().getBookingId());
                } else {
                    Toast.makeText(ConfirmBookingActivity.this, "Đặt phòng thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateBookingResponse> call, Throwable t) {
                b.loadingOverlay.setVisibility(View.GONE);
                b.btnConfirmBooking.setEnabled(true);
                Toast.makeText(ConfirmBookingActivity.this, "Lỗi kết nối.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showSuccessDialog(int newBookingId) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.booking_dialog_success);
        dialog.setCancelable(false); // Force user to click a button

        // Make dialog background transparent to show the card's rounded corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        MaterialButton btnBackToHome = dialog.findViewById(R.id.btnBackToHome);
        MaterialButton btnViewBookingDetail = dialog.findViewById(R.id.btnViewBookingDetail);

        btnBackToHome.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, MainActivity.class);
            // Clear the activity stack so pressing back doesn't return to confirmation
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnViewBookingDetail.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, BookingHistoryDetailActivity.class);
            System.out.println(newBookingId);
            intent.putExtra("bookingId", String.valueOf(newBookingId));
            startActivity(intent);
            finish();
        });

        dialog.show();
    }
    private void parseSessionData() {
        checkinDate = SessionManager.getInstance().getCheckinDate();
        checkoutDate = SessionManager.getInstance().getCheckoutDate();
        numAdults = SessionManager.getInstance().getNumAdults();
        numChildren = SessionManager.getInstance().getNumChildren();
        selectedHotel = SessionManager.getInstance().getSelectedHotelBrief();
    }

    private void setupToolbar() {
        setSupportActionBar(b.toolbar);
        b.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupSelectedRooms() {
        b.rvSelectedRooms.setLayoutManager(new LinearLayoutManager(this));
        ConfirmRoomChoiceAdapter adapter = new ConfirmRoomChoiceAdapter();
        b.rvSelectedRooms.setAdapter(adapter);
        adapter.submitList(RoomSelectionStore.getSelectionItems());
    }

    private void bindGuestInfo() {
        b.tvGuestFullNameValue.setText(getIntent().getStringExtra(FillBookingInfoActivity.EXTRA_GUEST_FULL_NAME));
        b.tvGuestEmailValue.setText(getIntent().getStringExtra(FillBookingInfoActivity.EXTRA_GUEST_EMAIL));
        b.tvGuestPhoneValue.setText(getIntent().getStringExtra(FillBookingInfoActivity.EXTRA_GUEST_PHONE));
    }

    private void bindDateSummary() {
        b.tvCheckinDate.setText(formatDisplayDate(checkinDate));
        b.tvCheckoutDate.setText(formatDisplayDate(checkoutDate));

        nights = getNights(checkinDate, checkoutDate);
        int rooms = RoomSelectionStore.getSelectedRoomCount();
        b.tvStaySummary.setText(getString(R.string.booking_nights_rooms_adults, nights, rooms, numAdults, numChildren));
    }

    private void bindTotalPrice() {
        String total = CurrencyUtils.formatVnd(RoomSelectionStore.getTotalPrice());
        b.tvHotelTotalPrice.setText(total);
        b.tvTotalPrice.setText(total);
    }

    private void bindCachedHotelInfo() {
        b.tvHotelName.setText(getString(R.string.booking_unknown_hotel));
        b.tvHotelAddress.setText(getString(R.string.booking_unknown_address));
        b.tvHotelRating.setText("0.0");

        SessionManager.SelectedHotelBrief hotelBrief = sessionManager.getSelectedHotelBrief();
        if (hotelBrief == null) {
            return;
        }

        if (hotelBrief.getHotelName() != null && !hotelBrief.getHotelName().trim().isEmpty()) {
            b.tvHotelName.setText(hotelBrief.getHotelName());
        }
        if (hotelBrief.getHotelAddress() != null && !hotelBrief.getHotelAddress().trim().isEmpty()) {
            b.tvHotelAddress.setText(hotelBrief.getHotelAddress());
        }
        b.tvHotelRating.setText(String.format("%.1f", hotelBrief.getAvgRating()));
    }

    private String formatDisplayDate(String isoDate) {
        if (isoDate == null || isoDate.trim().isEmpty()) {
            return getString(R.string.booking_not_available);
        }

        try {
            LocalDate date = LocalDate.parse(isoDate, DateTimeFormatter.ISO_LOCAL_DATE);
            return dayOfWeekLabel(date.getDayOfWeek()) + ", " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception ignore) {
            return isoDate;
        }
    }

    private int getNights(String checkin, String checkout) {
        if (checkin == null || checkout == null) {
            return 1;
        }

        try {
            LocalDate in = LocalDate.parse(checkin, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate out = LocalDate.parse(checkout, DateTimeFormatter.ISO_LOCAL_DATE);
            int nights = (int) ChronoUnit.DAYS.between(in, out);
            return Math.max(1, nights);
        } catch (Exception ignore) {
            return 1;
        }
    }

    private String dayOfWeekLabel(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "Thứ 2";
            case TUESDAY:
                return "Thứ 3";
            case WEDNESDAY:
                return "Thứ 4";
            case THURSDAY:
                return "Thứ 5";
            case FRIDAY:
                return "Thứ 6";
            case SATURDAY:
                return "Thứ 7";
            default:
                return "Chủ nhật";
        }
    }
}


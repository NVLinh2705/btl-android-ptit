package com.btl_ptit.hotelbooking.view.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.session.RoomSelectionStore;
import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.btl_ptit.hotelbooking.databinding.ActivityConfirmBookingBinding;
import com.btl_ptit.hotelbooking.utils.CurrencyUtils;
import com.btl_ptit.hotelbooking.view.adapter.ConfirmRoomChoiceAdapter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ConfirmBookingActivity extends AppCompatActivity {

    private ActivityConfirmBookingBinding b;
    private final SessionManager sessionManager = SessionManager.getInstance();

    private String checkinDate;
    private String checkoutDate;
    private int adults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityConfirmBookingBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        parseIntentData();
        setupToolbar();
        setupSelectedRooms();
        bindGuestInfo();
        bindDateSummary();
        bindTotalPrice();
        bindCachedHotelInfo();

        b.btnConfirmBooking.setOnClickListener(v ->
                Toast.makeText(this, "Đã xác nhận đặt phòng", Toast.LENGTH_SHORT).show());
    }

    private void parseIntentData() {
        checkinDate = getIntent().getStringExtra(FillBookingInfoActivity.EXTRA_CHECKIN_DATE);
        checkoutDate = getIntent().getStringExtra(FillBookingInfoActivity.EXTRA_CHECKOUT_DATE);
        adults = getIntent().getIntExtra(FillBookingInfoActivity.EXTRA_ADULTS, 2);
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

        int nights = getNights(checkinDate, checkoutDate);
        int rooms = RoomSelectionStore.getSelectedRoomCount();
        b.tvStaySummary.setText(getString(R.string.booking_nights_rooms_adults, nights, rooms, adults));
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


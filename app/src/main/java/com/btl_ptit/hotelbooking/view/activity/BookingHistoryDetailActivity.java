package com.btl_ptit.hotelbooking.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.model.MyBooking;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.BookingRestService;
import com.btl_ptit.hotelbooking.databinding.ActivityBookingHistoryDetailBinding;
import com.btl_ptit.hotelbooking.view.adapter.BookingHistoryDetailAdapter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BookingHistoryDetailActivity extends AppCompatActivity {

    private static final String TAG = "BookingHistoryDetailActivity";

    private ActivityBookingHistoryDetailBinding binding;
    private String bookingId;

    private MyBooking booking;
    private BookingRestService bookingRestService;
    private BookingHistoryDetailAdapter adapter;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingHistoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookingId = getIntent().getStringExtra("bookingId");
        bookingRestService = SupabaseClient.createService(BookingRestService.class);

        setupToolbar();
        // 1. Khởi tạo Adapter và RecyclerView
        initRecyclerView();

        // 2. Gọi API lấy dữ liệu
        fetchBookingDetail();

    }

    private void initRecyclerView() {
        adapter = new BookingHistoryDetailAdapter();
        binding.rvBookedRooms.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBookedRooms.setHasFixedSize(true);
        binding.rvBookedRooms.setAdapter(adapter);
    }

    private void fetchBookingDetail() {
        compositeDisposable.add(
                bookingRestService.getBookingDetail("eq." + bookingId, "*, hotels(*), booked_rooms(*, room_types(*))")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(bookingList -> {
                            if (bookingList != null && !bookingList.isEmpty()) {
                                this.booking = bookingList.get(0);
                                updateUI(this.booking);
                            }
                        }, throwable -> {
                            Log.e(TAG, "Lỗi lấy chi tiết: " + throwable.getMessage());
                        })
        );
    }

    private void updateUI(MyBooking booking) {
        binding.txtCheckin.setText(booking.getCheckinDate());
        binding.txtCheckout.setText(booking.getCheckoutDate());
        
        if (booking.getHotels() != null) {
            binding.txtHotelName.setText(booking.getHotels().getName());
        }
        
        binding.txtContactName.setText(booking.getCustomerFullname());
        binding.txtPhone.setText(booking.getCustomerPhone());
        binding.txtEmail.setText(booking.getCustomerEmail());
        binding.txtTotal.setText(String.format("%,.0f VNĐ", booking.getTotalAmount()));
        binding.txtGuestSummary.setText(booking.getNumAdults() + " người lớn, " + booking.getNumChildren() + " trẻ em");

        switch (booking.getStatusCode()) {
            case "PENDING":
                binding.txtStatus.setBackgroundResource(R.drawable.bg_pending);
                binding.txtStatus.setTextColor(Color.parseColor("#FBC02D"));
                binding.txtStatus.setText("Đang chờ duyệt");
                break;
            case "CONFIRMED":
                binding.txtStatus.setBackgroundResource(R.drawable.bg_confirmed);
                binding.txtStatus.setTextColor(Color.parseColor("#388E3C"));
                binding.txtStatus.setText("Đã xác nhận");
                break;
            case "CANCELLED":
                binding.txtStatus.setBackgroundResource(R.drawable.bg_cancelled);
                binding.txtStatus.setTextColor(Color.parseColor("#D32F2F"));
                binding.txtStatus.setText("Đã hủy");
                break;
        }


        // Tính số đêm
        try {
            LocalDate checkinDate = LocalDate.parse(booking.getCheckinDate());
            LocalDate checkoutDate = LocalDate.parse(booking.getCheckoutDate());
            long numberOfNights = ChronoUnit.DAYS.between(checkinDate, checkoutDate);
            binding.txtNumberOfNight.setText(numberOfNights + " đêm");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi parse ngày: " + e.getMessage());
        }

        // 3. Truyền danh sách phòng vào Adapter
        if (booking.getBookedRooms() != null) {
            adapter.setData(booking.getBookedRooms());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }
}

package com.btl_ptit.hotelbooking.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.model.MyBooking;
import com.btl_ptit.hotelbooking.data.model.Review;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.BookingRestService;
import com.btl_ptit.hotelbooking.data.remote.api_services.ReviewRestService;
import com.btl_ptit.hotelbooking.databinding.ActivityBookingHistoryDetailBinding;
import com.btl_ptit.hotelbooking.view.adapter.BookingHistoryDetailAdapter;
import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BookingHistoryDetailActivity extends AppCompatActivity {

    private static final String TAG = "BookingHistoryDetailActivity";

    private ActivityBookingHistoryDetailBinding binding;
    private String bookingId;

    private MyBooking booking;

    private Review review;
    private BookingRestService bookingRestService;

    private ReviewRestService reviewRestService;
    private BookingHistoryDetailAdapter adapter;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingHistoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookingId = getIntent().getStringExtra("bookingId");
        bookingRestService = SupabaseClient.createService(BookingRestService.class);
        reviewRestService = SupabaseClient.createService(ReviewRestService.class);

        setupToolbar();
        // 1. Khởi tạo Adapter và RecyclerView
        initRecyclerView();

        // 2. Gọi API lấy dữ liệu
        fetchBookingDetail();

        binding.txtToDoRating.setOnClickListener(v -> {
            Intent intent= new Intent(BookingHistoryDetailActivity.this,MyRatingActivity.class);
            intent.putExtra("booking",booking);
            if(review!=null){
                intent.putExtra("review",review);
            }
            startActivity(intent);
        });

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
        LocalDate checkinDate=null;
        LocalDate checkoutDate=null;
        // Tính số đêm
        try {
            checkinDate = LocalDate.parse(booking.getCheckinDate());
            checkoutDate = LocalDate.parse(booking.getCheckoutDate());
            long numberOfNights = ChronoUnit.DAYS.between(checkinDate, checkoutDate);
            binding.txtNumberOfNight.setText(numberOfNights + " đêm");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi parse ngày: " + e.getMessage());
        }

        switch (booking.getStatusCode()) {
            case "PENDING":
                binding.txtStatus.setBackgroundResource(R.drawable.bg_pending);
                binding.txtStatus.setTextColor(Color.parseColor("#FBC02D"));
                binding.txtStatus.setText("Đang chờ duyệt");
                binding.reviewCard.setVisibility(View.GONE);
                binding.qrCard.setVisibility(View.GONE);
                if(ChronoUnit.DAYS.between(LocalDate.now(),checkinDate)<1&&LocalDate.now().isBefore(checkinDate)){
                    binding.cancelCard.setVisibility(View.VISIBLE);
                    binding.btnCancelBooking.setOnClickListener(v -> {
                        cancelBooking();
                    });
                }else{
                    binding.cancelCard.setVisibility(View.GONE);
                }

                break;
            case "CONFIRMED":
                binding.txtStatus.setBackgroundResource(R.drawable.bg_confirmed);
                binding.txtStatus.setTextColor(Color.parseColor("#388E3C"));
                binding.txtStatus.setText("Đã xác nhận");
                binding.reviewCard.setVisibility(View.VISIBLE);
                binding.qrCard.setVisibility(View.VISIBLE);
                Bitmap qrCode= generateQr(booking);
                binding.imgQrSmall.setImageBitmap(qrCode);
                binding.qrCard.setOnClickListener(v -> {
                    showQrDialog(qrCode);
                });
                if(ChronoUnit.DAYS.between(LocalDate.now(),checkinDate)<1&&LocalDate.now().isBefore(checkinDate)){
                    binding.cancelCard.setVisibility(View.VISIBLE);
                    binding.btnCancelBooking.setOnClickListener(v -> {
                        cancelBooking();
                    });
                }else{
                    binding.cancelCard.setVisibility(View.GONE);
                }
                break;
            case "CANCELLED":
                binding.txtStatus.setBackgroundResource(R.drawable.bg_cancelled);
                binding.txtStatus.setTextColor(Color.parseColor("#D32F2F"));
                binding.txtStatus.setText("Đã hủy");
                binding.reviewCard.setVisibility(View.GONE);
                binding.cancelCard.setVisibility(View.GONE);
                binding.qrCard.setVisibility(View.GONE);
                break;
            case "CHECKED_IN":
                binding.txtStatus.setBackgroundResource(R.drawable.bg_confirmed);
                binding.txtStatus.setTextColor(Color.parseColor("#388E3C"));
                binding.txtStatus.setText("Đã checkin");
                binding.reviewCard.setVisibility(View.VISIBLE);
                binding.cancelCard.setVisibility(View.GONE);
                binding.qrCard.setVisibility(View.GONE);
                break;
            case "NO_SHOW":
                binding.txtStatus.setBackgroundResource(R.drawable.bg_cancelled);
                binding.txtStatus.setTextColor(Color.parseColor("#D32F2F"));
                binding.txtStatus.setText("Không có mặt");
                binding.reviewCard.setVisibility(View.GONE);
                binding.cancelCard.setVisibility(View.GONE);
                binding.qrCard.setVisibility(View.GONE);
                break;
            case "COMPLETED":
                binding.txtStatus.setBackgroundResource(R.drawable.bg_confirmed);
                binding.txtStatus.setTextColor(Color.parseColor("#388E3C"));
                binding.txtStatus.setText("Đã hoàn thành");
                binding.reviewCard.setVisibility(View.VISIBLE);
                binding.cancelCard.setVisibility(View.GONE);
                binding.qrCard.setVisibility(View.GONE);
                break;
            case "REJECTED":
                binding.txtStatus.setBackgroundResource(R.drawable.bg_cancelled);
                binding.txtStatus.setTextColor(Color.parseColor("#D32F2F"));
                binding.txtStatus.setText("Bị từ chối");
                binding.reviewCard.setVisibility(View.GONE);
                binding.cancelCard.setVisibility(View.GONE);
                binding.qrCard.setVisibility(View.GONE);
                break;


        }





        compositeDisposable.add(
            reviewRestService.getReviewsByBooking(
                    "*,user:users(id,full_name,avatar_url)",
                    "eq." + bookingId
            ).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(reviews -> {
                        if (reviews != null && !reviews.isEmpty()) {
                            review = reviews.get(0);
                            showReview(review); 
                        } else {
                            showWriteReviewButton();
                        }
                    }, throwable -> {
                        Log.e(TAG, "Lỗi review: " + throwable.getMessage());
                    })
        );



        // 3. Truyền danh sách phòng vào Adapter
        if (booking.getBookedRooms() != null) {
            adapter.setData(booking.getBookedRooms());
        }
    }

    private void showQrDialog(Bitmap qrCode) {
        Dialog dialog= new Dialog(this);
        dialog.setContentView(R.layout.dialog_qr);
        ImageView imgQrLarge= dialog.findViewById(R.id.imgQrLarge);
        imgQrLarge.setImageBitmap(qrCode);
        dialog.findViewById(R.id.btnClose).setOnClickListener(v -> {
            dialog.dismiss();
        });
        TextView txtBookingCode= dialog.findViewById(R.id.txtBookingCode);
        txtBookingCode.setText("Booking Code: " + booking.getBookingCode());
        dialog.show();
    }

    private Bitmap generateQr(MyBooking booking) {
        String text = booking.getId();
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            return encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400);
        } catch (Exception e) {
            return null;
        }
    }

    private void cancelBooking() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Hủy đặt phòng");
        alertDialog.setMessage("Bạn có chắc chắn muốn hủy đặt phòng?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Có", (dialog, which) -> {
            Map<String, Object> body = new HashMap<>();
            body.put("status_code", "CANCELLED");
            compositeDisposable.add(
                    bookingRestService.changeStatusBooking("eq."+bookingId,body)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                if (response.isSuccessful()) {
                                    finish();
                                }
                            }, throwable -> {
                                Log.e(TAG, "Cancel error: " + throwable.getMessage());
                            })
            );
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Không", (dialog, which) -> {});
        alertDialog.show();
    }

    private void showWriteReviewButton() {
        binding.reviewSection.setVisibility(View.GONE);
        binding.menuMore.setVisibility(View.GONE);
        binding.txtToDoRating.setText("Viết bài đánh giá của bạn");
    }

    private void showReview(Review review) {
        binding.reviewSection.setVisibility(View.VISIBLE);;
        binding.menuMore.setVisibility(View.VISIBLE);
        binding.menuMore.setOnClickListener(v -> {
            PopupMenu popupMenu= new PopupMenu(BookingHistoryDetailActivity.this,v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_item_listener,popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {

                if(item.getItemId()== R.id.deleteItem){
                    AlertDialog alertDialog= new AlertDialog.Builder(BookingHistoryDetailActivity.this).create();
                    alertDialog.setTitle("Xác nhận xóa");
                    alertDialog.setMessage("Bạn có chắc chắn muốn xóa?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Có", (dialog, which) -> {
                        compositeDisposable.add(
                                reviewRestService.deleteReviewByBooking("eq." + bookingId)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe( response -> {
                                            if (response.isSuccessful()) {
                                                showWriteReviewButton();
                                            }
                                        }, throwable -> {
                                            Log.e(TAG, "Delete error: " + throwable.getMessage());
                                        })
                                );
                        this.review=null;
                        }

                    );

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"Không", (dialog, which) -> {});
                    alertDialog.show();
                    return true;
                }else if(item.getItemId()==R.id.detailItem){
                    Intent intent= new Intent(BookingHistoryDetailActivity.this,MyRatingActivity.class);
                    intent.putExtra("booking",booking);
                    if(review!=null){
                        intent.putExtra("review",review);
                    }
                    startActivity(intent);
                    return true;
                }
                return false;
            });
            popupMenu.show();

        });
        binding.txtToDoRating.setText("Chỉnh sửa bài đánh giá của bạn");
        OffsetDateTime dt = OffsetDateTime.parse(review.getCreatedAt());
        LocalDate reviewDate = dt.toLocalDate();
        binding.txtReviewDate.setText(reviewDate.toString());
        double myRating = review.getRating();
        if (myRating >= 9.0) {
            binding.txtReviewRatingLabel.setText("Rất tốt");
        } else if (myRating >= 8.0) {
            binding.txtReviewRatingLabel.setText("Tốt");
        } else if (myRating >= 7.0) {
            binding.txtReviewRatingLabel.setText("Khá tốt");
        } else if (myRating >= 6.0) {
            binding.txtReviewRatingLabel.setText("Trung bình");
        } else if (myRating >= 5.0) {
            binding.txtReviewRatingLabel.setText("Dưới trung bình");
        } else {
            binding.txtReviewRatingLabel.setText("Kém");
        }
        binding.txtReviewComment.setText(review.getComment());
        binding.txtReviewerName.setText(review.getCustomer().getFullName());
        binding.txtRatingNumber.setText(String.valueOf(review.getRating()));
        String url= review.getCustomer().getAvatarUrl();
        Glide.with(this).
                load(url)
                .placeholder(android.R.color.transparent)
                .centerCrop()
                .into(binding.imgReviewerAvatar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    protected void onResume(){
        super.onResume();
        fetchBookingDetail();
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

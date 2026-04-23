package com.btl_ptit.hotelbooking.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.dto.ReviewRequest;
import com.btl_ptit.hotelbooking.data.model.MyBooking;
import com.btl_ptit.hotelbooking.data.model.Review;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.ReviewRestService;
import com.btl_ptit.hotelbooking.data.repository.ReviewRepository;
import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.btl_ptit.hotelbooking.databinding.ActivityMyRatingBinding;
import com.btl_ptit.hotelbooking.view.adapter.ReviewAdapter;
import com.btl_ptit.hotelbooking.view_model.paging.ReviewViewModel;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyRatingActivity extends AppCompatActivity {

    private static final String TAG = "MyRatingActivity";

    private ActivityMyRatingBinding binding;

    private MyBooking booking;

    private Review review;

    private ReviewRestService reviewRestService;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rating);
        binding = ActivityMyRatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reviewRestService = SupabaseClient.createService(ReviewRestService.class);
        booking= (MyBooking) getIntent().getSerializableExtra("booking");
        review= (Review) getIntent().getSerializableExtra("review");
        setupToolbar();
        String[] scores={"1","2","3","4","5","6","7","8","9","10"};
        ArrayAdapter<String> spinnerAdapter= new ArrayAdapter<>(this, R.layout.score_spinner_item,scores);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                double myRating = Double.parseDouble(scores[position]);
                String label;
                if (myRating >= 9.0) {
                    label = "Rất tốt";
                } else if (myRating >= 8.0) {
                    label = "Tốt";
                } else if (myRating >= 7.0) {
                    label = "Khá tốt";
                } else if (myRating >= 6.0) {
                    label = "Trung bình";
                } else if (myRating >= 5.0) {
                    label = "Dưới trung bình";
                } else {
                    label = "Kém";
                }
                binding.txtScoreLabel.setText(label);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinnerRating.setAdapter(spinnerAdapter);
        if(review!=null){
            binding.edtComment.setText(review.getComment());
            binding.spinnerRating.setSelection(review.getRating()-1);
        }
        binding.btnSubmit.setOnClickListener(v -> {
            ReviewRequest reviewRequest= new ReviewRequest();
            reviewRequest.setBookingId(booking.getId());
            reviewRequest.setRating(Integer.parseInt(binding.spinnerRating.getSelectedItem().toString()));
            reviewRequest.setComment(binding.edtComment.getText().toString());
            reviewRequest.setHotelId(booking.getHotels().getId());
            reviewRequest.setCustomerId(SessionManager.getInstance().getUser().getId());
            compositeDisposable.add(
                    reviewRestService.upsertReview("booking_id",reviewRequest)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                Review resReview = response.get(0);
                                if (resReview != null) {
                                    finish();
                                }
                            }, throwable -> {
                                Log.e(TAG, "Lỗi review: " + throwable.getMessage());
                            }));

        });


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
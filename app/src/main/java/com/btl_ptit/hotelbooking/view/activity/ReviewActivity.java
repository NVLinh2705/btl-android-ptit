package com.btl_ptit.hotelbooking.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.model.Review;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.ReviewRestService;
import com.btl_ptit.hotelbooking.data.repository.ReviewRepository;
import com.btl_ptit.hotelbooking.databinding.ActivityReviewBinding;
import com.btl_ptit.hotelbooking.utils.paging.MyComparator;
import com.btl_ptit.hotelbooking.view.adapter.LoadStateAdapter;
import com.btl_ptit.hotelbooking.view.adapter.ReviewAdapter;
import com.btl_ptit.hotelbooking.view_model.paging.ReviewViewModel;
import com.btl_ptit.hotelbooking.view_model.paging.ReviewViewModelFactory;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class ReviewActivity extends AppCompatActivity {

    private static final String TAG = "ReviewActivity";

    private ActivityReviewBinding binding;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ReviewRestService reviewRestService;

    private ReviewAdapter adapter;

    private Context mContext;

    private ReviewRepository repository;

    private ReviewViewModel viewModel;

    private String hotelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContext= this;
        reviewRestService = SupabaseClient.createService(ReviewRestService.class);
        repository= new ReviewRepository(reviewRestService);
        hotelId= getIntent().getStringExtra("hotelId");
        viewModel= new ViewModelProvider(this ,new ReviewViewModelFactory(repository, hotelId)).get(ReviewViewModel.class);
        initReviewAdapter();
        compositeDisposable.clear();
        compositeDisposable.add(
                viewModel.pagingDataFlow
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(pagingData ->{
                            Log.d("DEBUG", "PagingData received");
                            Log.d("DEBUG", "PagingData received: " + pagingData);
                            adapter.submitData(getLifecycle(), pagingData);
                        }
                        )
        );
    }

    private void initReviewAdapter() {
        adapter = new ReviewAdapter(new MyComparator<Review>(), mContext, new ArrayList<>());
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReviews.setHasFixedSize(true);
        binding.rvReviews.setAdapter(
                adapter.withLoadStateFooter(
                        new LoadStateAdapter(v -> adapter.retry())
                ));
        adapter.addLoadStateListener(loadState -> {

            if (loadState.getRefresh() instanceof androidx.paging.LoadState.NotLoading
                    && adapter.getItemCount() == 0) {
//                TextView textView = new TextView(this);
//                textView.setText("No Review");
//                binding.rvReviews.addView(textView);
                Toast.makeText(mContext, "No Review", Toast.LENGTH_SHORT).show();
            }
            return null;
                }
        );
    }
}
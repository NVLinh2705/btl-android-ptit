package com.btl_ptit.hotelbooking.view.fragment.hoteldetail;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.dto.HotelReview;
import com.btl_ptit.hotelbooking.data.dto.HotelReviewStats;
import com.btl_ptit.hotelbooking.data.dto.PaginatedReviewsResponse;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.SupabaseRestService;
import com.btl_ptit.hotelbooking.databinding.FragmentHotelReviewsBinding;
import com.btl_ptit.hotelbooking.view.activity.RoomTypeDetailActivity;
import com.btl_ptit.hotelbooking.view.adapter.HotelReviewsAdapter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsTabFragment extends Fragment {

    private static final String ARG_HOTEL_ID = "arg_hotel_id";
    private final DateTimeFormatter uiDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
    private final DateTimeFormatter apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault());

    private final List<HotelReview> reviews = new ArrayList<>();
    private HotelReviewsAdapter adapter;
    private SupabaseRestService service;
    private int hotelId;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMore = true;
    private FragmentHotelReviewsBinding binding;

    public static ReviewsTabFragment newInstance(int hotelId) {
        ReviewsTabFragment fragment = new ReviewsTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_HOTEL_ID, hotelId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHotelReviewsBinding.inflate(inflater, container, false);

        service = SupabaseClient.createService(SupabaseRestService.class);
        hotelId = getArguments() != null ? getArguments().getInt(ARG_HOTEL_ID, -1) : -1;

        setupRecyclerView();
        setupFilterControls();
        setupPagination();

        if (hotelId > 0) {
            loadReviewStats();
            loadReviews();
        } else {
            binding.txtState.setVisibility(View.VISIBLE);
            binding.txtState.setText("Không có dữ liệu đánh giá");
        }

        return binding.getRoot();
    }



    private void setupRecyclerView() {
        adapter = new HotelReviewsAdapter(roomTypeInfo -> {
            if (roomTypeInfo != null && roomTypeInfo.getId() != null) {
                android.content.Intent intent = new android.content.Intent(requireContext(), RoomTypeDetailActivity.class);
                intent.putExtra(RoomTypeDetailActivity.EXTRA_ROOM_TYPE_ID, roomTypeInfo.getId());
                startActivity(intent);
            }
        });
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvReviews.setAdapter(adapter);
    }

    private void setupFilterControls() {
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Mới nhất", "Cũ nhất", "Rating giảm dần", "Rating tăng dần"}
        );
        binding.spinnerSort.setAdapter(sortAdapter);

        binding.btnPickStartDate.setOnClickListener(v -> pickDate(binding.txtStartDate));
        binding.btnPickEndDate.setOnClickListener(v -> pickDate(binding.txtEndDate));
        binding.btnApplyFilter.setOnClickListener(v -> {
            currentPage = 1;
            hasMore = true;
            reviews.clear();
            loadReviews();
        });
    }


    private void setupPagination() {
//        binding.rvReviews.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if(dy > 0){
//                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                    if (!isLoading && hasMore && layoutManager != null &&
//                            layoutManager.findLastCompletelyVisibleItemPosition() == reviews.size() - 1) {
//                        currentPage++;
//                        loadReviews();
//                    }
//                }
//
//            }
//        });
        // Listen to the NestedScrollView instead of the RecyclerView
        binding.nestedScrollView.setOnScrollChangeListener(new androidx.core.widget.NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull androidx.core.widget.NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                // Check if the user has scrolled to the very bottom
                if (scrollY > 0 && scrollY >= (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {

                    if (!isLoading && hasMore) {
                        currentPage++;
                        loadReviews();
                    }
                }
            }
        });
    }

    private void loadReviewStats() {
        service.getHotelReviewStats(hotelId).enqueue(new Callback<HotelReviewStats>() {
            @Override
            public void onResponse(@NonNull Call<HotelReviewStats> call, @NonNull Response<HotelReviewStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HotelReviewStats stats = response.body();
                    binding.txtAvgRating.setText(String.format(Locale.getDefault(), "%.1f", stats.getAverageRating()));
                    binding.txtTotalReviews.setText(getString(R.string.total_review_count, stats.getTotalReviews()));
                    if (stats.getAverageRating() >= 9.0) {
                        binding.txtAvgRatingLabel.setText("Rất tốt");
                    } else if (stats.getAverageRating() >= 8.0) {
                        binding.txtAvgRatingLabel.setText("Tốt");
                    } else if (stats.getAverageRating() >= 7.0) {
                        binding.txtAvgRatingLabel.setText("Khá tốt");
                    }else if (stats.getAverageRating() >= 6.0) {
                        binding.txtAvgRatingLabel.setText("Trung bình");
                    } else if (stats.getAverageRating() >= 5.0) {
                        binding.txtAvgRatingLabel.setText("Dưới trung bình");
                    } else {
                        binding.txtAvgRatingLabel.setText("Kém");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<HotelReviewStats> call, @NonNull Throwable t) {
                // Không hiển thị gì nếu tải thống kê thất bại
            }
        });
    }
    private void loadReviews() {
        if (isLoading || !hasMore) return;
        isLoading = true;
        binding.progressBar.setVisibility(View.VISIBLE);

        String sortValue = getSortOrder();
        Integer minRating = parseIntOrNull(binding.edtMinRating.getText().toString());
        Integer maxRating = parseIntOrNull(binding.edtMaxRating.getText().toString());
        String beginDate = parseUiDateToApiDate(binding.txtStartDate.getText().toString());
        String endDate = parseUiDateToApiDate(binding.txtEndDate.getText().toString());
        String keyword = binding.edtReviewKeyword.getText() != null ? binding.edtReviewKeyword.getText().toString().trim() : "";
        String p_keyword = "";
        if(!keyword.isEmpty())
            p_keyword = "{\"" + keyword + "\"}" ;
        service.getHotelReviews(hotelId, sortValue, currentPage, minRating, maxRating, beginDate, endDate, !(p_keyword.isEmpty())? p_keyword :null)
                .enqueue(new Callback<PaginatedReviewsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PaginatedReviewsResponse> call, @NonNull Response<PaginatedReviewsResponse> response) {
                        isLoading = false;
                        binding.progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            PaginatedReviewsResponse paginatedResponse = response.body();
                            List<HotelReview> newReviews = paginatedResponse.getData();

                            if (currentPage == 1) {
                                reviews.clear();
                            }
                            reviews.addAll(newReviews);
                            adapter.submitList(new ArrayList<>(reviews));

                            hasMore = paginatedResponse.isHasNext();
                            updateUiState();
                        } else {
                            handleApiError();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PaginatedReviewsResponse> call, @NonNull Throwable t) {
                        isLoading = false;
                        binding.progressBar.setVisibility(View.GONE);
                        handleApiError();
                    }
                });
    }

    private void updateUiState() {
        if (reviews.isEmpty()) {
            binding.txtState.setVisibility(View.VISIBLE);
            binding.txtState.setText(currentPage == 1 ? "Chưa có đánh giá nào" : "Không tìm thấy đánh giá nào phù hợp");
        } else {
            binding.txtState.setVisibility(View.GONE);
        }
    }

    private void handleApiError() {
        if (currentPage == 1) {
            binding.txtState.setVisibility(View.VISIBLE);
            binding.txtState.setText("Tải đánh giá thất bại");
        }
        Toast.makeText(requireContext(), "Tải đánh giá thất bại", Toast.LENGTH_SHORT).show();
    }

    private String getSortOrder() {
        String selected = binding.spinnerSort.getSelectedItem().toString();
        switch (selected) {
            case "Cũ nhất":
                return "created_at.asc";
            case "Rating giảm dần":
                return "rating.desc";
            case "Rating tăng dần":
                return "rating.asc";
            case "Mới nhất":
            default:
                return "created_at.desc";
        }
    }

    private void pickDate(android.widget.TextView target) {
        java.util.Calendar now = java.util.Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    LocalDate selected = LocalDate.of(year, month + 1, dayOfMonth);
                    target.setText(selected.format(uiDateFormatter));
                },
                now.get(java.util.Calendar.YEAR),
                now.get(java.util.Calendar.MONTH),
                now.get(java.util.Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private Integer parseIntOrNull(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private String parseUiDateToApiDate(String value) {
        try {
            if (value == null || value.trim().isEmpty()) return null;
            LocalDate date = LocalDate.parse(value.trim(), uiDateFormatter);
            return date.format(apiDateFormatter);
        } catch (Exception ignored) {
            return null;
        }
    }
}

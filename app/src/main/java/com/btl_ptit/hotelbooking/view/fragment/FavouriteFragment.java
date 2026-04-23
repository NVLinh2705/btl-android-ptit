package com.btl_ptit.hotelbooking.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.btl_ptit.hotelbooking.databinding.FragmentFavouriteBinding;
import com.btl_ptit.hotelbooking.view.activity.HotelDetailActivity;
import com.btl_ptit.hotelbooking.view.activity.HotelInfoActivity;
import com.btl_ptit.hotelbooking.view.adapter.FavoriteHotelAdapter;
import com.btl_ptit.hotelbooking.viewmodel.FavoriteViewModel;

public class FavouriteFragment extends Fragment {

    private FragmentFavouriteBinding binding;
    private FavoriteViewModel viewModel;
    private FavoriteHotelAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        
        setupRecyclerView();
        observeViewModel();

        String userId = SessionManager.getInstance().getUser().getId();
        if (userId != null) {
            viewModel.loadFavorites(userId);
        }
    }

    private void setupRecyclerView() {
        adapter = new FavoriteHotelAdapter(
                hotel -> {
                    // Click vào card để xem chi tiết
                    Intent intent = new Intent(getContext(), HotelInfoActivity.class);
                    intent.putExtra("hotel_id", hotel.getId());
                    startActivity(intent);
                },
                hotel -> {
                    // Xử lý khi ấn nút Tim (Xóa khỏi danh sách)
                    String userId = SessionManager.getInstance().getUser().getId();
                    if (userId != null) {
                        // 1. Gọi API xóa thông qua ViewModel
                        viewModel.toggleLike(userId, hotel.getId());

                        // 2. Cập nhật UI ngay lập tức (Optimistic UI)
                        // Lấy list hiện tại từ adapter, tạo list mới và xóa item vừa chọn
                        java.util.List<com.btl_ptit.hotelbooking.data.model.MyHotel> currentList =
                                new java.util.ArrayList<>(adapter.getCurrentList());
                        currentList.remove(hotel);
                        adapter.submitList(currentList);

                        Toast.makeText(getContext(), "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        binding.rvFavoriteHotels.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvFavoriteHotels.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getFavoriteHotels().observe(getViewLifecycleOwner(), hotels -> {
            if (hotels != null) {
                adapter.submitList(hotels);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Optional: binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

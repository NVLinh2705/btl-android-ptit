package com.btl_ptit.hotelbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.databinding.ItemFavoriteHotelBinding;
import com.btl_ptit.hotelbooking.listener.OnHotelClickListener;
import com.bumptech.glide.Glide;

public class FavoriteHotelAdapter extends ListAdapter<MyHotel, FavoriteHotelAdapter.FavoriteViewHolder> {
    private final OnHotelClickListener listener;
    private final OnFavoriteClickListener favoriteListener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(MyHotel hotel);
    }

    public FavoriteHotelAdapter(OnHotelClickListener listener, OnFavoriteClickListener favoriteListener) {
        super(new DiffUtil.ItemCallback<MyHotel>() {
            @Override
            public boolean areItemsTheSame(@NonNull MyHotel oldItem, @NonNull MyHotel newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull MyHotel oldItem, @NonNull MyHotel newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.listener = listener;
        this.favoriteListener = favoriteListener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoriteViewHolder(ItemFavoriteHotelBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        MyHotel hotel = getItem(position);
        holder.bind(hotel, listener, favoriteListener);
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private final ItemFavoriteHotelBinding binding;

        public FavoriteViewHolder(@NonNull ItemFavoriteHotelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(MyHotel hotel, OnHotelClickListener listener, OnFavoriteClickListener favoriteListener) {
            binding.tvHotelName.setText(hotel.getName());
            binding.tvLocation.setText(hotel.getLocation());
            binding.tvRating.setText(String.valueOf(hotel.getRating()));
//            binding.tvPrice.setText(hotel.getAveragePrice() * 100000 + "₫");

            Glide.with(binding.ivHotelImage.getContext())
                    .load(hotel.getAvatar())
                    .centerCrop()
                    .into(binding.ivHotelImage);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onHotelClick(hotel);
                }
            });

            binding.ivFavorite.setOnClickListener(v -> {
                if (favoriteListener != null) {
                    favoriteListener.onFavoriteClick(hotel);
                }
            });
        }
    }
}

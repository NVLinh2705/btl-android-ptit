package com.btl_ptit.hotelbooking.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.databinding.HotelItemBinding;
import com.btl_ptit.hotelbooking.listener.OnHotelClickListener;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

public class HotelAdapter extends PagingDataAdapter<MyHotel, HotelAdapter.HotelViewHolder> {
    private Context context;
    private OnHotelClickListener listener;

    public HotelAdapter(@NotNull DiffUtil.ItemCallback<MyHotel> diffCallback, Context context, OnHotelClickListener listener) {
        super(diffCallback);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HotelViewHolder(HotelItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        MyHotel currentMyHotel = getItem(position);
        // Check for null
        if (currentMyHotel != null) {
            Glide.with(holder.hotelItemBinding.imageViewHotel.getContext())
                    .load(currentMyHotel.getAvatar()).fitCenter()
                    .into(holder.hotelItemBinding.imageViewHotel);

            holder.hotelItemBinding.textViewRating.setText("1.500.000 trung bình một đêm");
            holder.hotelItemBinding.txtName.setText(currentMyHotel.getName());

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onHotelClick(currentMyHotel);
                }
            });
        }
    }

    public class HotelViewHolder extends RecyclerView.ViewHolder {
        // Define layout view binding
        HotelItemBinding hotelItemBinding;

        public HotelViewHolder(@NonNull HotelItemBinding hotelItemBinding) {
            super(hotelItemBinding.getRoot());
            // init binding
            this.hotelItemBinding = hotelItemBinding;
        }
    }

}

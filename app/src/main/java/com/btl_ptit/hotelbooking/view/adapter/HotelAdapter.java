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
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

public class HotelAdapter extends PagingDataAdapter<MyHotel, HotelAdapter.HotelViewHolder> {
    public static final int LOADING_ITEM = 0;
    public static final int HOTEL_ITEM = 1;
    private Context context;

    public HotelAdapter(@NotNull DiffUtil.ItemCallback<MyHotel> diffCallback, Context context) {
        super(diffCallback);
        this.context = context;
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

        }
    }

    @Override
    public int getItemViewType(int position) {
        // set ViewType
        return position == getItemCount() ? HOTEL_ITEM : LOADING_ITEM;
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

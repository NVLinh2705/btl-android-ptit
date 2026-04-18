package com.btl_ptit.hotelbooking.view.adapter;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.model.MyHotel;
import com.btl_ptit.hotelbooking.databinding.HotelItemBinding;
import com.btl_ptit.hotelbooking.listener.OnHotelClickListener;
import com.btl_ptit.hotelbooking.utils.paging.MyComparator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import org.jetbrains.annotations.NotNull;

public class HotelAdapter extends PagingDataAdapter<MyHotel, HotelAdapter.HotelViewHolder> {
    private Context context;
    private OnHotelClickListener listener;

    public HotelAdapter(@NotNull MyComparator<MyHotel> diffCallback, Context context, OnHotelClickListener listener) {
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
            holder.hotelItemBinding.shimmerLayout.stopShimmer();
            holder.hotelItemBinding.shimmerLayout.setShimmer(null);

            holder.hotelItemBinding.imageViewHotel.setBackground(null);
            holder.hotelItemBinding.tvPrice.setBackground(null);
            holder.hotelItemBinding.labelAveragePrice.setBackground(null);
            holder.hotelItemBinding.txtName.setBackground(null);
            holder.hotelItemBinding.tvRating.setBackground(null);
            holder.hotelItemBinding.tvLocation.setBackground(null);
            holder.hotelItemBinding.layoutPrice.setBackground(null);

            int radius = (int) (8 * context.getResources().getDisplayMetrics().density);
            Glide.with(holder.hotelItemBinding.imageViewHotel.getContext())
                    .load(currentMyHotel.getAvatar())
                    .transform(new CenterCrop(), new RoundedCorners(radius))
                    .into(holder.hotelItemBinding.imageViewHotel);

            holder.hotelItemBinding.tvPrice.setText(currentMyHotel.getAveragePrice() * 100000 + "₫");
            holder.hotelItemBinding.labelAveragePrice.setText(R.string.perNight);
            holder.hotelItemBinding.txtName.setText(currentMyHotel.getName());
            holder.hotelItemBinding.tvRating.setText(String.valueOf(currentMyHotel.getRating() / 10.0));
            holder.hotelItemBinding.ivStar.setVisibility(VISIBLE);
            holder.hotelItemBinding.tvLocation.setText("\uD83D\uDCCD " + currentMyHotel.getLocation());


            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onHotelClick(currentMyHotel);
                }
            });
        }
    }

    static class HotelViewHolder extends RecyclerView.ViewHolder {
        // Define layout view binding
        HotelItemBinding hotelItemBinding;

        public HotelViewHolder(@NonNull HotelItemBinding hotelItemBinding) {
            super(hotelItemBinding.getRoot());
            // init binding
            this.hotelItemBinding = hotelItemBinding;
        }
    }

}

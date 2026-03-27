package com.btl_ptit.hotelbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class HotelImagePagerAdapter extends RecyclerView.Adapter<HotelImagePagerAdapter.ImageVH> {

    private final List<String> imageUrls = new ArrayList<>();

    public void submitList(List<String> urls) {
        imageUrls.clear();
        if (urls != null) imageUrls.addAll(urls);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_hotel_image_pager, parent, false);
        return new ImageVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageVH holder, int position) {
        String url = imageUrls.get(position);
        Glide.with(holder.imgHotel.getContext())
            .load(url)
            .placeholder(android.R.color.transparent)
            .centerCrop()
            .into(holder.imgHotel);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ImageVH extends RecyclerView.ViewHolder {
        final ImageView imgHotel;

        ImageVH(@NonNull View itemView) {
            super(itemView);
            imgHotel = itemView.findViewById(R.id.imgHotel);
        }
    }
}



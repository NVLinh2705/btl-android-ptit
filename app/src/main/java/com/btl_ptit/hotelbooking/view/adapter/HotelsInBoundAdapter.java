package com.btl_ptit.hotelbooking.view.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.dto.HotelInBoundResponse;
import com.btl_ptit.hotelbooking.databinding.HotelItemMapBinding;
import com.btl_ptit.hotelbooking.listener.OnHotelClickListener;
import com.btl_ptit.hotelbooking.utils.HotelDiffCallback;
import com.btl_ptit.hotelbooking.utils.MyUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.maps.model.LatLng;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;
import java.util.List;

public class HotelsInBoundAdapter extends RecyclerView.Adapter<HotelsInBoundAdapter.HotelViewHolder> {

    private List<HotelInBoundResponse> hotelList;
//    private OnHotelClickListener listener;
    private OnMenuItemClickListener menuItemClickListener;
    private Context context;

    public HotelsInBoundAdapter(List<HotelInBoundResponse> hotelList, Context context) {
        this.hotelList = hotelList;
//        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HotelViewHolder(HotelItemMapBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        HotelInBoundResponse hotel = hotelList.get(position);
        if (hotel != null) {
            Glide.with(holder.mHotelItemBinding.imgHotel.getContext())
                    .load(hotel.getAvatar())
                    .into(holder.mHotelItemBinding.imgHotel);
            holder.mHotelItemBinding.tvPrice.setText(hotel.getAveragePrice() * 100000 + "₫ " + context.getString(R.string.perNight));
            holder.mHotelItemBinding.tvHotelName.setText(hotel.getName());
            holder.mHotelItemBinding.tvRating.setText(String.valueOf(hotel.getRating()));
            holder.mHotelItemBinding.tvAddress.setText(hotel.getLocation());

            holder.mHotelItemBinding.btnHotelDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PowerMenu powerMenu = MyUtils.getPowerMenuInHotelInBound(context);
                    powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                        @Override
                        public void onItemClick(int position, PowerMenuItem item) {
                            switch (position) {
                                case 0:
                                    Log.d("MyMapActivityTAG", "onItemClick: Like");
                                    break;
                                case 1:
                                    Log.d("MyMapActivityTAG", "onItemClick: Directions");
                                    openGoogleMapsNavigation(new LatLng(hotel.getLatitude(), hotel.getLongitude()));
                                    break;
                                case 2:
                                    Log.d("MyMapActivityTAG", "onItemClick: Detail");
                                    break;
                            }
                            powerMenu.dismiss();
                        }
                    });
                    powerMenu.showAsDropDown(view, -350, -650);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }
    public List<HotelInBoundResponse> getItems() {
        return hotelList != null ? hotelList : new ArrayList<>();
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.menuItemClickListener = listener;
    }

    private void openGoogleMapsNavigation(LatLng destination) {
        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination.latitude + "," + destination.longitude + "&mode=d");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        } catch (ActivityNotFoundException e) {
            // Nếu không tìm thấy app Google Maps, mở bằng Browser (web maps)
            Uri webIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" +
                    destination.latitude + "," + destination.longitude);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webIntentUri);
            context.startActivity(webIntent);
        }
    }

    public void updateData(List<HotelInBoundResponse> newHotels) {
        if (newHotels == null) return;
        HotelDiffCallback diffCallback = new HotelDiffCallback(this.hotelList, newHotels);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.hotelList.clear();
        this.hotelList = newHotels;
        notifyDataSetChanged();
        diffResult.dispatchUpdatesTo(this);
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        HotelItemMapBinding mHotelItemBinding;

        public HotelViewHolder(@NonNull HotelItemMapBinding binding) {
            super(binding.getRoot());
            this.mHotelItemBinding = binding;
        }
    }
}

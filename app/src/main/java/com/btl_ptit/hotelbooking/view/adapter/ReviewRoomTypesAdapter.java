package com.btl_ptit.hotelbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.dto.HotelReview;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ReviewRoomTypesAdapter extends RecyclerView.Adapter<ReviewRoomTypesAdapter.ViewHolder> {

    private final List<HotelReview.RoomTypeInfo> items = new ArrayList<>();
    private final HotelReviewsAdapter.OnRoomTypeClickListener listener;

    public ReviewRoomTypesAdapter(HotelReviewsAdapter.OnRoomTypeClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<HotelReview.RoomTypeInfo> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_room_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HotelReview.RoomTypeInfo item = items.get(position);

        holder.txtRoomTypeName.setText(item.getRoomTypeName() != null ? item.getRoomTypeName() : "");

        int nights = item.getNights() != null ? item.getNights() : 1;
        holder.txtNights.setText(nights + " đêm");

        Glide.with(holder.imgRoomType.getContext())
                .load(item.getCoverImage())
                .placeholder(R.drawable.placeholder_room)
                .error(R.drawable.placeholder_room)
                .into(holder.imgRoomType);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRoomTypeClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgRoomType;
        final TextView txtRoomTypeName;
        final TextView txtNights;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoomType = itemView.findViewById(R.id.imgRoomType);
            txtRoomTypeName = itemView.findViewById(R.id.txtRoomTypeName);
            txtNights = itemView.findViewById(R.id.txtNights);
        }
    }
}
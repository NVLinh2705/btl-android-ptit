package com.btl_ptit.hotelbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.dto.HotelReview;
import com.bumptech.glide.Glide;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HotelReviewsAdapter extends RecyclerView.Adapter<HotelReviewsAdapter.ReviewVH> {

    public interface OnRoomTypeClickListener {
        void onRoomTypeClick(HotelReview.RoomTypeInfo roomTypeInfo);
    }

    private final List<HotelReview> items = new ArrayList<>();
    private final OnRoomTypeClickListener onRoomTypeClickListener;

    public HotelReviewsAdapter(OnRoomTypeClickListener onRoomTypeClickListener) {
        this.onRoomTypeClickListener = onRoomTypeClickListener;
    }

    public void submitList(List<HotelReview> reviews) {
        items.clear();
        if (reviews != null) items.addAll(reviews);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotel_review, parent, false);
        return new ReviewVH(view, onRoomTypeClickListener); // Pass listener down to ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewVH holder, int position) {
        HotelReview item = items.get(position);

        holder.txtReviewerName.setText(item.getCustomer() != null ? item.getCustomer().getFullName() : "Anonymous");
        holder.txtRatingNumber.setText(String.format(Locale.getDefault(), "%.1f", item.getRating()));
        holder.txtDate.setText(formatDate(item.getCreatedAt()));
        holder.txtRatingLabel.setText(getRatingLabel(item.getRating()));
        holder.txtComment.setText(item.getComment());

        Glide.with(holder.imgReviewerAvatar.getContext())
                .load(item.getCustomer() != null ? item.getCustomer().getAvatarUrl() : null)
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(holder.imgReviewerAvatar);

        List<HotelReview.RoomTypeInfo> bookedRooms = item.getBookedRooms();

        if (bookedRooms == null || bookedRooms.isEmpty()) {
            holder.rvBookedRooms.setVisibility(View.GONE);
        } else {
            holder.rvBookedRooms.setVisibility(View.VISIBLE);
            holder.roomTypesAdapter.submitList(bookedRooms);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatDate(String value) {
        if (value == null || value.trim().isEmpty()) return "";
        try {
            OffsetDateTime dt = OffsetDateTime.parse(value);
            return dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()));
        } catch (Exception ignored) {
            if (value.length() >= 10) {
                String isoDate = value.substring(0, 10);
                String[] parts = isoDate.split("-");
                if (parts.length == 3) {
                    return parts[2] + "/" + parts[1] + "/" + parts[0];
                }
            }
            return value;
        }
    }

    private String getRatingLabel(double rating) {
        if (rating >= 9.0) return "Xuất sắc";
        if (rating >= 8.0) return "Tốt";
        if (rating >= 7.0) return "Khá tốt";
        if (rating >= 6.0) return "Trung bình";
        if (rating >= 5.0) return "Dưới trung bình";
        return "Kém";
    }

    static class ReviewVH extends RecyclerView.ViewHolder {
        final CircleImageView imgReviewerAvatar;
        final TextView txtReviewerName;
        final TextView txtRatingNumber;
        final TextView txtDate;
        final TextView txtRatingLabel;
        final TextView txtComment;

        final RecyclerView rvBookedRooms;
        final ReviewRoomTypesAdapter roomTypesAdapter;

        ReviewVH(@NonNull View itemView, OnRoomTypeClickListener listener) {
            super(itemView);
            imgReviewerAvatar = itemView.findViewById(R.id.imgReviewerAvatar);
            txtReviewerName = itemView.findViewById(R.id.txtReviewerName);
            txtRatingNumber = itemView.findViewById(R.id.txtRatingNumber);
            txtDate = itemView.findViewById(R.id.txtReviewDate);
            txtRatingLabel = itemView.findViewById(R.id.txtReviewRatingLabel);
            txtComment = itemView.findViewById(R.id.txtReviewComment);

            rvBookedRooms = itemView.findViewById(R.id.rvBookedRooms);

            // Initialize the nested horizontal adapter
            roomTypesAdapter = new ReviewRoomTypesAdapter(listener);
            rvBookedRooms.setAdapter(roomTypesAdapter);
        }
    }
}
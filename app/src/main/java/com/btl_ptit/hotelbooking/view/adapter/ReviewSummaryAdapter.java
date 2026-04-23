package com.btl_ptit.hotelbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.dto.ReviewSummary;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewSummaryAdapter extends RecyclerView.Adapter<ReviewSummaryAdapter.ReviewSummaryViewHolder> {

    private final List<ReviewSummary> items = new ArrayList<>();

    public void submitList(List<ReviewSummary> reviews) {
        items.clear();
        if (reviews != null) {
            items.addAll(reviews);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewSummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_summary, parent, false);
        return new ReviewSummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewSummaryViewHolder holder, int position) {
        ReviewSummary review = items.get(position);

        holder.txtReviewerName.setText(review.getReviewerFullName());
        holder.txtReviewRating.setText(String.valueOf(review.getRating()));
        holder.txtReviewComment.setText(review.getComment());

        Glide.with(holder.imgReviewerAvatar.getContext())
                .load(review.getReviewerAvatarUrl())
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(holder.imgReviewerAvatar);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ReviewSummaryViewHolder extends RecyclerView.ViewHolder {
        final CircleImageView imgReviewerAvatar;
        final TextView txtReviewerName;
        final TextView txtReviewRating;
        final TextView txtReviewComment;

        ReviewSummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgReviewerAvatar = itemView.findViewById(R.id.img_reviewer_avatar);
            txtReviewerName = itemView.findViewById(R.id.txt_reviewer_name);
            txtReviewRating = itemView.findViewById(R.id.txt_review_rating);
            txtReviewComment = itemView.findViewById(R.id.txt_review_comment);
        }
    }
}


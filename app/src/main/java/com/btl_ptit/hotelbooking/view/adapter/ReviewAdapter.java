package com.btl_ptit.hotelbooking.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.data.model.Review;
import com.btl_ptit.hotelbooking.databinding.ItemReviewBinding;
import com.btl_ptit.hotelbooking.utils.paging.MyComparator;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReviewAdapter extends PagingDataAdapter<Review, ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<Review> reviews;

    public ReviewAdapter(@NotNull MyComparator<Review> diffCallback, Context context, List<Review> reviews) {
        super(diffCallback);
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewViewHolder(ItemReviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review currentReview = getItem(position);
        // Check for null
        if (currentReview != null) {
            holder.reviewItemBinding.txtReviewComment.setText(currentReview.getComment());
            holder.reviewItemBinding.txtReviewerName.setText(currentReview.getCustomer().getFullName());
            holder.reviewItemBinding.txtRatingNumber.setText(String.valueOf(currentReview.getRating()));
            holder.reviewItemBinding.txtReviewDate.setText(currentReview.getCreatedAt());
            double avgRating = currentReview.getRating();
            if (avgRating >= 9.0) {
                holder.reviewItemBinding.txtReviewRatingLabel.setText("Rất tốt");
            } else if (avgRating >= 8.0) {
                holder.reviewItemBinding.txtReviewRatingLabel.setText("Tốt");
            } else if (avgRating >= 7.0) {
                holder.reviewItemBinding.txtReviewRatingLabel.setText("Khá tốt");
            } else if (avgRating >= 6.0) {
                holder.reviewItemBinding.txtReviewRatingLabel.setText("Trung bình");
            } else if (avgRating >= 5.0) {
                holder.reviewItemBinding.txtReviewRatingLabel.setText("Dưới trung bình");
            } else {
                holder.reviewItemBinding.txtReviewRatingLabel.setText("Kém");
            }
            String url= currentReview.getCustomer().getAvatarUrl();
            Glide.with(holder.itemView.getContext()).
                    load(url)
                    .placeholder(android.R.color.transparent)
                    .centerCrop()
                    .into(holder.reviewItemBinding.imgReviewerAvatar);
        }
    }


    static class ReviewViewHolder extends RecyclerView.ViewHolder {

        ItemReviewBinding reviewItemBinding;


        public ReviewViewHolder(@NonNull ItemReviewBinding itemView) {
            super(itemView.getRoot());
            this.reviewItemBinding = itemView;
        }

    }

}

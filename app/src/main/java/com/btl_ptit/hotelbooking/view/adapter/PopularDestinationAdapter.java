package com.btl_ptit.hotelbooking.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.data.model.MyPopularDestination;
import com.btl_ptit.hotelbooking.databinding.PopularDestinationItemBinding;
import com.btl_ptit.hotelbooking.listener.OnDestinationClickListener;
import com.btl_ptit.hotelbooking.utils.CurrencyUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;


public class PopularDestinationAdapter extends PagingDataAdapter<MyPopularDestination, PopularDestinationAdapter.DestinationViewHolder> {

    private Context context;
    private OnDestinationClickListener listener;

    public PopularDestinationAdapter(@NonNull DiffUtil.ItemCallback<MyPopularDestination> diffCallback,Context context, OnDestinationClickListener listener) {
        super(diffCallback);
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public DestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PopularDestinationAdapter.DestinationViewHolder(PopularDestinationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationViewHolder holder, int position) {
        MyPopularDestination currentMyPopularDestination = getItem(position);
        // Check for null
        if (currentMyPopularDestination != null) {
            holder.popularDestinationItemBinding.shimmerLayout.stopShimmer();
            holder.popularDestinationItemBinding.shimmerLayout.setShimmer(null);

            holder.popularDestinationItemBinding.imageViewDestination.setBackground(null);
            holder.popularDestinationItemBinding.tvPrice.setBackground(null);
            holder.popularDestinationItemBinding.labelAveragePrice.setBackground(null);
            holder.popularDestinationItemBinding.txtName.setBackground(null);

            int radius = (int) (8 * context.getResources().getDisplayMetrics().density);
            Glide.with(holder.popularDestinationItemBinding.imageViewDestination.getContext())
                    .load(currentMyPopularDestination.getAvatar())
                    .transform(new CenterCrop(), new RoundedCorners(radius))
                    .into(holder.popularDestinationItemBinding.imageViewDestination);

            holder.popularDestinationItemBinding.tvPrice.setText(CurrencyUtils.formatVnd(Long.parseLong(currentMyPopularDestination.getAveragePrice())));
            holder.popularDestinationItemBinding.labelAveragePrice.setText("trung bình / một đêm");
            holder.popularDestinationItemBinding.txtName.setText(currentMyPopularDestination.getName());

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDestinationClick(currentMyPopularDestination);
                }
            });
        }
    }

    static class DestinationViewHolder extends RecyclerView.ViewHolder {
        // Define layout view binding
        PopularDestinationItemBinding popularDestinationItemBinding;

        public DestinationViewHolder(@NonNull PopularDestinationItemBinding popularDestinationItemBinding) {
            super(popularDestinationItemBinding.getRoot());
            // init binding
            this.popularDestinationItemBinding = popularDestinationItemBinding;
        }
    }
}

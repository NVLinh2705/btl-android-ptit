package com.btl_ptit.hotelbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.data.model.MyNotification;
import com.btl_ptit.hotelbooking.databinding.ItemNotificationBinding;
import com.bumptech.glide.Glide;

public class NotificationAdapter extends ListAdapter<MyNotification, NotificationAdapter.NotificationViewHolder> {

    public NotificationAdapter() {
        super(new DiffUtil.ItemCallback<MyNotification>() {
            @Override
            public boolean areItemsTheSame(@NonNull MyNotification oldItem, @NonNull MyNotification newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull MyNotification oldItem, @NonNull MyNotification newItem) {
                return oldItem.getTitle().equals(newItem.getTitle()) &&
                        oldItem.getContent().equals(newItem.getContent());
            }
        });
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final ItemNotificationBinding binding;

        public NotificationViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(MyNotification notification) {
            binding.tvNotificationTitle.setText(notification.getTitle());
            binding.tvNotificationTime.setText(notification.getCreatedAt()); // Bạn có thể dùng logic format date ở đây

//            Glide.with(binding.ivNotificationIcon.getContext())
//                    .load(notification.getHotelAvatar())
//                    .placeholder(com.btl_ptit.hotelbooking.R.drawable.bg_placeholder)
//                    .into(binding.ivNotificationIcon);
        }
    }
}

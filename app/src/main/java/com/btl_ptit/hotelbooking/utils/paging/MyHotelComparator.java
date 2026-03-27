package com.btl_ptit.hotelbooking.utils.paging;

import androidx.recyclerview.widget.DiffUtil;
import androidx.annotation.NonNull;

import com.btl_ptit.hotelbooking.data.model.MyHotel;

public class MyHotelComparator extends DiffUtil.ItemCallback<MyHotel> {
    @Override
    public boolean areItemsTheSame(@NonNull MyHotel oldItem, @NonNull MyHotel newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull MyHotel oldItem, @NonNull MyHotel newItem) {
        return oldItem.getId().equals(newItem.getId());
    }
}

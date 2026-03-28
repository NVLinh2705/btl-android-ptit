package com.btl_ptit.hotelbooking.utils.paging;

import androidx.recyclerview.widget.DiffUtil;
import androidx.annotation.NonNull;


import com.btl_ptit.hotelbooking.interfaces.DiffUtilModel;

import java.util.Objects;

public class MyComparator<T extends DiffUtilModel> extends DiffUtil.ItemCallback<T> {
    @Override
    public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
        return Objects.equals(oldItem.getUniqueIdentifier(), newItem.getUniqueIdentifier());
    }

    @Override
    public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
        return oldItem.equals(newItem);
    }
}

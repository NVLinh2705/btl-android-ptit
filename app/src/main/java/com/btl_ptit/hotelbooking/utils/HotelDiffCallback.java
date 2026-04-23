package com.btl_ptit.hotelbooking.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.btl_ptit.hotelbooking.data.dto.HotelInBoundResponse;

import java.util.List;
import java.util.Objects;

public class HotelDiffCallback extends DiffUtil.Callback {
    private final List<HotelInBoundResponse> oldList;
    private final List<HotelInBoundResponse> newList;

    public HotelDiffCallback(List<HotelInBoundResponse> oldList, List<HotelInBoundResponse> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() { return oldList.size(); }
    @Override
    public int getNewListSize() { return newList.size(); }

    @Override
    public boolean areItemsTheSame(int oldPos, int newPos) {
        return Objects.equals(oldList.get(oldPos).getId(), newList.get(newPos).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldPos, int newPos) {
        return oldList.get(oldPos).equals(newList.get(newPos));
    }
}

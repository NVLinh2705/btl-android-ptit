package com.btl_ptit.hotelbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.data.session.RoomSelectionStore;
import com.btl_ptit.hotelbooking.databinding.ItemBookingSelectedRoomBinding;
import com.btl_ptit.hotelbooking.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

public class BookingSelectedRoomAdapter extends RecyclerView.Adapter<BookingSelectedRoomAdapter.ViewHolder> {

    private final List<RoomSelectionStore.SelectionItem> items = new ArrayList<>();

    public void submitList(List<RoomSelectionStore.SelectionItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemBookingSelectedRoomBinding binding = ItemBookingSelectedRoomBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemBookingSelectedRoomBinding b;

        ViewHolder(ItemBookingSelectedRoomBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }

        void bind(RoomSelectionStore.SelectionItem item) {
            b.tvRoomName.setText(item.getRoomType().getName());
            b.tvQuantity.setText("Số lượng: " + item.getCount());
            b.tvUnitPrice.setText("Đơn giá: " + CurrencyUtils.formatVnd(item.getUnitPrice()));
            b.tvLineTotal.setText(CurrencyUtils.formatVnd(item.getLineTotal()));
        }
    }
}


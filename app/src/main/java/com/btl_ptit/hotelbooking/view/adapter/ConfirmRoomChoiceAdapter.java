package com.btl_ptit.hotelbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.model.RoomType;
import com.btl_ptit.hotelbooking.data.session.RoomSelectionStore;
import com.btl_ptit.hotelbooking.databinding.ItemConfirmRoomChoiceBinding;
import com.btl_ptit.hotelbooking.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

public class ConfirmRoomChoiceAdapter extends RecyclerView.Adapter<ConfirmRoomChoiceAdapter.ViewHolder> {

    private final List<RoomSelectionStore.SelectionItem> items = new ArrayList<>();

    public void submitList(List<RoomSelectionStore.SelectionItem> selectionItems) {
        items.clear();
        if (selectionItems != null) {
            items.addAll(selectionItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConfirmRoomChoiceBinding b = ItemConfirmRoomChoiceBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(b);
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
        private final ItemConfirmRoomChoiceBinding b;

        ViewHolder(ItemConfirmRoomChoiceBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(RoomSelectionStore.SelectionItem item) {
            RoomType room = item.getRoomType();
            b.tvRoomName.setText(room.getName());
            b.tvPayment.setText(b.getRoot().getContext().getString(
                    R.string.booking_room_payment,
                    safeText(room.getPaymentPolicy(), b.getRoot().getContext().getString(R.string.booking_not_available))
            ));
            b.tvCancellation.setText(b.getRoot().getContext().getString(
                    R.string.booking_room_cancellation,
                    safeText(room.getCancellationPolicy(), b.getRoot().getContext().getString(R.string.booking_not_available))
            ));
            b.tvGuests.setText(b.getRoot().getContext().getString(R.string.booking_room_max_guests, room.getMaxGuests()));
            b.tvBeds.setText(b.getRoot().getContext().getString(
                    R.string.booking_room_beds,
                    room.getBedCount(),
                    safeText(room.getBedType(), "giường")
            ));
            b.tvSelectedCount.setText(b.getRoot().getContext().getString(R.string.booking_room_selected_count, item.getCount()));
            b.tvSubtotal.setText(b.getRoot().getContext().getString(
                    R.string.booking_room_subtotal,
                    CurrencyUtils.formatVnd(item.getLineTotal())
            ));
        }

        private String safeText(String value, String fallback) {
            return value == null || value.trim().isEmpty() ? fallback : value;
        }
    }
}


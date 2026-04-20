package com.btl_ptit.hotelbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.data.model.MyBookedRoom;
import com.btl_ptit.hotelbooking.databinding.BookingHistoryDetailItemBinding;

import java.util.List;

public class BookingHistoryDetailAdapter extends RecyclerView.Adapter<BookingHistoryDetailAdapter.ViewHolder> {

    private List<MyBookedRoom> mListBookedRooms;

    public void setData(List<MyBookedRoom> list) {
        this.mListBookedRooms = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BookingHistoryDetailItemBinding binding = BookingHistoryDetailItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyBookedRoom bookedRoom = mListBookedRooms.get(position);
        if (bookedRoom == null) {
            return;
        }

        // 1. Hiển thị thông tin phòng (Tên phòng + số lượng)
        String roomName = (bookedRoom.getRoomType() != null) ?
                bookedRoom.getRoomType().getName()+", "
                        +bookedRoom.getRoomType().getBedType()+", "
                        +bookedRoom.getRoomType().getView() : "Phòng";
        String summary = String.format("%s (x%d)", roomName, bookedRoom.getQuantity());
        holder.binding.txtRoomSummary.setText(summary);

        // 2. Hiển thị giá tiền (Định dạng VND)
        String price = String.format("%,.0f VNĐ", bookedRoom.getSubTotal());
        holder.binding.txtPrice.setText(price);
    }

    @Override
    public int getItemCount() {
        if (mListBookedRooms != null) {
            return mListBookedRooms.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final BookingHistoryDetailItemBinding binding;

        public ViewHolder(@NonNull BookingHistoryDetailItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

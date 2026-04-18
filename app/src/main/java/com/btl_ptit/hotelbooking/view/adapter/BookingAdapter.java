package com.btl_ptit.hotelbooking.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.data.model.MyBooking;
import com.btl_ptit.hotelbooking.databinding.BookingItemBinding;
import com.btl_ptit.hotelbooking.listener.OnBookingClickListener;
import com.btl_ptit.hotelbooking.utils.paging.MyComparator;

import org.jetbrains.annotations.NotNull;

import kotlin.coroutines.CoroutineContext;

public class BookingAdapter extends PagingDataAdapter<MyBooking, BookingAdapter.BookingViewHolder> {

    private Context context;
    private OnBookingClickListener onBookingClickListener;


    public BookingAdapter(@NotNull MyComparator<MyBooking> diffCallback, Context context, OnBookingClickListener listener) {
        super(diffCallback);
        this.context = context;
        this.onBookingClickListener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookingViewHolder(BookingItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {

        MyBooking currentMyBooking = getItem(position);
        // Check for null
        if (currentMyBooking != null) {
            holder.bookingItemBinding.txtBookingId.setText("Mã: " +currentMyBooking.getId());
            String hotelName = (currentMyBooking.getHotel() != null &&
                    currentMyBooking.getHotel().getName() != null)
                    ? currentMyBooking.getHotel().getName()
                    : "No hotel";

            holder.bookingItemBinding.txtHotelName.setText(hotelName);
            holder.bookingItemBinding.txtStatus.setText(currentMyBooking.getStatusCode()==null ? "null " : currentMyBooking.getStatusCode().toString());
            holder.bookingItemBinding.txtPrice.setText(String.valueOf(currentMyBooking.getTotalAmount()));

            holder.itemView.setOnClickListener(v -> {
                if (onBookingClickListener != null) {
                    onBookingClickListener.onBookingClick(currentMyBooking);
                }
            });
        }
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {

        BookingItemBinding bookingItemBinding;


        public BookingViewHolder(@NonNull BookingItemBinding itemView) {
            super(itemView.getRoot());
            this.bookingItemBinding = itemView;
        }
    }
}

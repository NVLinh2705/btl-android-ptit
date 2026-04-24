package com.btl_ptit.hotelbooking.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.model.MyBooking;
import com.btl_ptit.hotelbooking.databinding.BookingItemBinding;
import com.btl_ptit.hotelbooking.listener.OnBookingClickListener;
import com.btl_ptit.hotelbooking.utils.DateTimeUtils;
import com.btl_ptit.hotelbooking.utils.NotificationScheduler;
import com.btl_ptit.hotelbooking.utils.paging.MyComparator;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.OffsetDateTime;

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
            String hotelName = (currentMyBooking.getHotels() != null &&
                    currentMyBooking.getHotels().getName() != null)
                    ? currentMyBooking.getHotels().getName()
                    : "No hotel";

            holder.bookingItemBinding.txtHotelName.setText(hotelName);
            holder.bookingItemBinding.txtPrice.setText(String.valueOf(currentMyBooking.getTotalAmount()));
            OffsetDateTime dt = OffsetDateTime.parse(currentMyBooking.getCreatedAt());
            LocalDate bookingDate = dt.toLocalDate();
            holder.bookingItemBinding.txtBookingDate.setText(bookingDate.toString());
            switch (currentMyBooking.getStatusCode()) {
                case "PENDING":
                    holder.bookingItemBinding.txtStatus.setBackgroundResource(R.drawable.bg_pending);
                    holder.bookingItemBinding.txtStatus.setTextColor(Color.parseColor("#FBC02D"));
                    holder.bookingItemBinding.txtStatus.setText("Đang chờ duyệt");
                    break;
                case "CONFIRMED":
                    holder.bookingItemBinding.txtStatus.setBackgroundResource(R.drawable.bg_confirmed);
                    holder.bookingItemBinding.txtStatus.setTextColor(Color.parseColor("#388E3C"));
                    holder.bookingItemBinding.txtStatus.setText("Đã xác nhận");
                    scheduleBookingReminders(holder.itemView.getContext(), currentMyBooking, hotelName);
                    break;
                case "CANCELLED":
                    holder.bookingItemBinding.txtStatus.setBackgroundResource(R.drawable.bg_cancelled);
                    holder.bookingItemBinding.txtStatus.setTextColor(Color.parseColor("#D32F2F"));
                    holder.bookingItemBinding.txtStatus.setText("Đã hủy");
                    break;
                case "CHECKED_IN":
                    holder.bookingItemBinding.txtStatus.setBackgroundResource(R.drawable.bg_confirmed);
                    holder.bookingItemBinding.txtStatus.setTextColor(Color.parseColor("#388E3C"));
                    holder.bookingItemBinding.txtStatus.setText("Đã checkin");
                    break;
                case "NO_SHOW":
                    holder.bookingItemBinding.txtStatus.setBackgroundResource(R.drawable.bg_cancelled);
                    holder.bookingItemBinding.txtStatus.setTextColor(Color.parseColor("#D32F2F"));
                    holder.bookingItemBinding.txtStatus.setText("Không có mặt");
                    break;
                case "COMPLETED":
                    holder.bookingItemBinding.txtStatus.setBackgroundResource(R.drawable.bg_confirmed);
                    holder.bookingItemBinding.txtStatus.setTextColor(Color.parseColor("#388E3C"));
                    holder.bookingItemBinding.txtStatus.setText("Đã hoàn thành");
                    break;
                case "REJECTED":
                    holder.bookingItemBinding.txtStatus.setBackgroundResource(R.drawable.bg_cancelled);
                    holder.bookingItemBinding.txtStatus.setTextColor(Color.parseColor("#D32F2F"));
                    holder.bookingItemBinding.txtStatus.setText("Bị từ chối");
                    break;
            }

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

    private void scheduleBookingReminders(Context context, MyBooking booking, String hotelName) {
        // Nhắc trước Check-in 1 ngày
        long delayCheckIn = DateTimeUtils.getDelayUntil(booking.getCheckinDate(), 1);
        if (delayCheckIn > 0) {
            NotificationScheduler.scheduleReminder(
                    context,
                    "Sắp đến ngày nhận phòng!",
                    "Bạn có lịch check-in tại " + hotelName + " vào ngày mai.",
                    delayCheckIn,
                    booking.getId() + "_checkIn"
            );
        }

        // Nhắc trước Check-out 1 ngày
        long delayCheckOut = DateTimeUtils.getDelayUntil(booking.getCheckoutDate(), 1);
        if (delayCheckOut > 0) {
            NotificationScheduler.scheduleReminder(
                    context,
                    "Nhắc nhở trả phòng",
                    "Đừng quên làm thủ tục check-out tại " + hotelName + " vào ngày mai nhé!",
                    delayCheckOut,
                    booking.getId() + "_checkOut"
            );
        }
    }
}

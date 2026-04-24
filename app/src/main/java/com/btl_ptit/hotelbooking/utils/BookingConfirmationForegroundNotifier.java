package com.btl_ptit.hotelbooking.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.MyApplication;
import com.btl_ptit.hotelbooking.data.model.MyBooking;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.BookingRestService;
import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.btl_ptit.hotelbooking.view.activity.MainActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BookingConfirmationForegroundNotifier {
    private static final String CHANNEL_ID = "booking_confirmed_channel";
    private static final int POLL_INTERVAL_MS = 8000;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final Set<String> seenConfirmedBookingIds = new HashSet<>();

    private final BookingRestService bookingService;
    private boolean baselineLoaded = false;
    private boolean started = false;
    private boolean loading = false;

    public BookingConfirmationForegroundNotifier() {
        this.bookingService = SupabaseClient.createService(BookingRestService.class);
    }

    private final Runnable pollTask = new Runnable() {
        @Override
        public void run() {
            pollConfirmedBookings();
            handler.postDelayed(this, POLL_INTERVAL_MS);
        }
    };

    public void start(Context context) {
        if (started)
            return;
        started = true;
        ensureChannel(context);
        handler.post(pollTask);
    }

    public void stop() {
        if (!started)
            return;
        started = false;
        handler.removeCallbacks(pollTask);
        disposables.clear();
        loading = false;
    }

    private void pollConfirmedBookings() {
        if (loading)
            return;

        SessionManager sessionManager = SessionManager.getInstance();
        if (sessionManager.getUser() == null || sessionManager.getUser().getId() == null) {
            return;
        }

        loading = true;
        String customerId = sessionManager.getUser().getId();

        disposables.add(
                bookingService.getListBooking(
                        "id,booking_code,status_code,hotels(name)",
                        "eq." + customerId,
                        "eq.CONFIRMED",
                        "created_at.desc",
                        20,
                        0)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                bookings -> {
                                    loading = false;
                                    handleConfirmedBookings(bookings);
                                },
                                throwable -> loading = false));
    }

    private void handleConfirmedBookings(List<MyBooking> bookings) {
        if (!baselineLoaded) {
            for (MyBooking booking : bookings) {
                if (booking != null && booking.getId() != null) {
                    seenConfirmedBookingIds.add(booking.getId());
                }
            }
            baselineLoaded = true;
            return;
        }

        for (MyBooking booking : bookings) {
            if (booking == null || booking.getId() == null)
                continue;

            if (!seenConfirmedBookingIds.contains(booking.getId())) {
                seenConfirmedBookingIds.add(booking.getId());
                showConfirmedNotification(MyApplication.getAppContext(), booking);
            }
        }
    }

    private void ensureChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null)
            return;

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Booking Confirmations",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Thong bao khi booking duoc xac nhan");
        manager.createNotificationChannel(channel);
    }

    private void showConfirmedNotification(Context context, MyBooking booking) {
        if (context == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                booking.getId() != null ? booking.getId().hashCode() : (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String bookingCode = booking.getBookingCode() == null ? booking.getId() : booking.getBookingCode();
        String hotelName = booking.getHotels() != null && booking.getHotels().getName() != null
                ? booking.getHotels().getName()
                : "khach san";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.outline_notifications_read_24)
                .setContentTitle("Booking da duoc xac nhan")
                .setContentText("Ma " + bookingCode + " tai " + hotelName + " da duoc xac nhan")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat.from(context).notify(
                booking.getId() != null ? booking.getId().hashCode()
                        : (int) (System.currentTimeMillis() % Integer.MAX_VALUE),
                builder.build());
    }
}

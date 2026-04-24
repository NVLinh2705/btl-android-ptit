package com.btl_ptit.hotelbooking.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler {
    private static final String PREF_NAME = "notification_prefs";

    public static void scheduleReminder(Context context, String title, String content, long delayMillis, String uniqueId) {
        if (delayMillis <= 0) return;

        // KIỂM TRA XEM ĐÃ THÔNG BÁO CHƯA
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(uniqueId, false)) {
            return; // Nếu đã thông báo rồi thì không làm gì cả
        }

        Data data = new Data.Builder()
                .putString("title", title)
                .putString("content", content)
                .putString("uniqueId", uniqueId) // Truyền ID vào để Worker đánh dấu đã xong
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
                uniqueId,
                ExistingWorkPolicy.KEEP,
                request
        );
    }
}
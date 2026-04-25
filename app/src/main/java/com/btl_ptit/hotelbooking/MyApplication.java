package com.btl_ptit.hotelbooking;

import android.app.Application;
import android.content.Context;

import com.btl_ptit.hotelbooking.utils.BookingConfirmationForegroundNotifier;

public class MyApplication extends Application {
    private static String TAG = "MyApplicationTAG";
    private static Context appContext;
    private BookingConfirmationForegroundNotifier bookingNotifier;
    private int startedActivities = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        bookingNotifier = new BookingConfirmationForegroundNotifier();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityStarted(android.app.Activity activity) {
                startedActivities++;
                if (startedActivities == 1) {
                    bookingNotifier.start(getApplicationContext());
                }
            }

            @Override
            public void onActivityStopped(android.app.Activity activity) {
                startedActivities = Math.max(0, startedActivities - 1);
                if (startedActivities == 0) {
                    bookingNotifier.stop();
                }
            }

            @Override
            public void onActivityCreated(android.app.Activity activity, android.os.Bundle savedInstanceState) {
            }

            @Override
            public void onActivityResumed(android.app.Activity activity) {
            }

            @Override
            public void onActivityPaused(android.app.Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(android.app.Activity activity, android.os.Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(android.app.Activity activity) {
            }
        });
    }

    public static Context getAppContext() {
        return appContext;
    }
}

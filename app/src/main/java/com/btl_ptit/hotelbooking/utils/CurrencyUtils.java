package com.btl_ptit.hotelbooking.utils;

import java.text.NumberFormat;
import java.util.Locale;

public final class CurrencyUtils {

    private static final NumberFormat VND_FORMAT = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    private CurrencyUtils() {
    }

    public static String formatVnd(double amount) {
        return VND_FORMAT.format((long) amount) + " VND";
    }
}


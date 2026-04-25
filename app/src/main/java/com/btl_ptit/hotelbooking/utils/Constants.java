package com.btl_ptit.hotelbooking.utils;

public class Constants {
    public static final String PREF_NAME = "session";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final boolean LOG_ENABLED = true;

    // paging
    public static final int PAGE_SIZE = 10;
    public static final int PREFETCH_DISTANCE = 10;
    public static final boolean ENABLE_PLACEHOLDERS = false;
    public static final int INITIAL_LOAD_SIZE = 10;
    public static final int MAX_SIZE = 100;

    public static final int NUM_OF_PLACE_HOLDER = 5;

    public static final float ZOOM_LEVEL = 14f;
    public static final long TIME_OUT = 500;

    public static final float ZOOM_LEVEL_LOWER_THRESHOLD = 1.5f;
    public static final int DISTANCE_LOWER_THRESHOLD = 5000;

    public static final String HOTEL_ID = "hotel_id";
    public static final String HOTEL_NAME = "hotel_name";

    public static final String KEY_PROVINCE = "p_province";
    public static final String KEY_DISTRICT = "p_district";
    public static final String KEY_CHECKIN = "p_checkin_date";
    public static final String KEY_CHECKOUT = "p_checkout_date";
    public static final String KEY_NUM_ROOM = "p_num_room";
    public static final String KEY_NUM_ADULT = "p_num_adult";
    public static final String KEY_NUM_CHILDREN = "p_num_children";
}

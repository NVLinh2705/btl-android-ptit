package com.btl_ptit.hotelbooking.utils;

import static androidx.paging.PagingSource.LoadResult.Page.COUNT_UNDEFINED;

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
}

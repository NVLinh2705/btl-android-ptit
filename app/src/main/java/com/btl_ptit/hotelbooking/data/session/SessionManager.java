package com.btl_ptit.hotelbooking.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.btl_ptit.hotelbooking.data.model.User;

public class SessionManager {
    private static final String PREF_NAME = "session";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_AVATAR_URL = "avatar_url";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";

    private static final String KEY_ID = "id";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String accessToken, String refreshToken, @Nullable User user) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        if (user != null) {
            editor.putString(KEY_ID, user.getId());
            editor.putString(KEY_FULL_NAME, user.getFullName());
            editor.putString(KEY_AVATAR_URL, user.getAvatarUrl());
            editor.putString(KEY_EMAIL, user.getEmail());
            editor.putString(KEY_PHONE,user.getPhone());
        }
        editor.apply();
    }

    @Nullable
    public User getUser() {
        String name = prefs.getString(KEY_FULL_NAME, null);
        String avatar = prefs.getString(KEY_AVATAR_URL, null);
        String email = prefs.getString(KEY_EMAIL, null);
        String phone = prefs.getString(KEY_PHONE, null);
        String id = prefs.getString(KEY_ID, null);

        if (name == null && avatar == null && email == null && phone == null) return null;
        return new User(id,email, phone, name, avatar);
    }
    public void clear() {
        prefs.edit().clear().apply();
    }
}


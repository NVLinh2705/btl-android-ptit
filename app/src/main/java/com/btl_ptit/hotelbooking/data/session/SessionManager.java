package com.btl_ptit.hotelbooking.data.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import androidx.annotation.Nullable;

import com.btl_ptit.hotelbooking.MyApplication;
import com.btl_ptit.hotelbooking.data.model.User;

import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;

public class SessionManager {
    private static final String PREF_NAME = "session";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_AVATAR_URL = "avatar_url";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";

    private static final String KEY_ID = "id";

    private static volatile SessionManager instance;

    private final SharedPreferences prefs;
    private SelectedHotelBrief selectedHotelBrief;

    private String checkinDate;
    private String checkoutDate;

    private int numAdults, numChildren;

    public int getNumAdults() {
        return numAdults == 0? 1 : numAdults;
    }

    public void setNumAdults(int numAdults) {
        this.numAdults = numAdults;
    }

    public int getNumChildren() {
        return numChildren == 0? 1 : numChildren;
    }

    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }

    public String getCheckinDate() {
        return checkinDate== null || checkinDate.isEmpty() ? "2026-04-28" : checkinDate;
    }

    public void setCheckinDate(String checkinDate) {
        this.checkinDate = checkinDate;
    }

    public String getCheckoutDate() {
        return checkoutDate==null || checkoutDate.isEmpty() ? "2026-04-30" : checkoutDate;
    }

    public void setCheckoutDate(String checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    private SessionManager() {
        this.prefs = MyApplication.getAppContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    @Nullable
    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    @Nullable
    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public void saveSession(String accessToken, String refreshToken, @Nullable User user) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);

        String userId = null;
        if (user != null && user.getId() != null) {
            userId = user.getId();
        } else if (accessToken != null) {
            userId = getUserIdFromToken(accessToken);
        }

        if (userId != null) {
            editor.putString(KEY_ID, userId);
        }

        if (user != null) {
            editor.putString(KEY_FULL_NAME, user.getFullName());
            editor.putString(KEY_AVATAR_URL, user.getAvatarUrl());
            editor.putString(KEY_EMAIL, user.getEmail());
            editor.putString(KEY_PHONE, user.getPhone());
        }
        editor.apply();
    }

    private String getUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            String payload = new String(Base64.decode(parts[1], Base64.DEFAULT));
            JSONObject json = new JSONObject(payload);
            return json.getString("sub");
        } catch (Exception e) {
            return null;
        }
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
        clearSelectedHotelBrief();
    }

    public void saveSelectedHotelBrief(int hotelId, String hotelName, String hotelAddress, double avgRating) {
        selectedHotelBrief = new SelectedHotelBrief(hotelId, hotelName, hotelAddress, avgRating);
    }

    @Nullable
    public SelectedHotelBrief getSelectedHotelBrief() {
        return selectedHotelBrief;
    }

    public void clearSelectedHotelBrief() {
        selectedHotelBrief = null;
    }

    @Data
    @AllArgsConstructor
    public static final class SelectedHotelBrief {
        private int hotelId;
        private final String hotelName;
        private final String hotelAddress;
        private final double avgRating;
    }
}


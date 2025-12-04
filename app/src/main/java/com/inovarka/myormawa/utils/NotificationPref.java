package com.inovarka.myormawa.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class NotificationPref {

    private static final String PREF_NAME = "notif_pref";
    private static final String KEY_LAST_READ_ID = "last_read_id";

    private SharedPreferences prefs;

    public NotificationPref(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLastReadId(String id) {
        prefs.edit().putString(KEY_LAST_READ_ID, id).apply();
    }

    public String getLastReadId() {
        return prefs.getString(KEY_LAST_READ_ID, "0");
    }
}

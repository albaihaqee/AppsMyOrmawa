package com.inovarka.myormawa.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUserId(String userId) {
        editor.putString(Constants.KEY_USER_ID, userId);
        editor.apply();
    }


    public void createLoginSession(String userId, String token, String nim, String fullName,
                                   String email, String programStudi, String angkatan,
                                   int level, String idOrmawa) {

        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.putString(Constants.KEY_USER_ID, userId); // DISIMPAN SEBAGAI STRING
        editor.putString(Constants.KEY_TOKEN, token);
        editor.putString(Constants.KEY_NIM, nim);
        editor.putString(Constants.KEY_FULL_NAME, fullName);
        editor.putString(Constants.KEY_EMAIL, email);
        editor.putString(Constants.KEY_PROGRAM_STUDI, programStudi);
        editor.putString(Constants.KEY_ANGKATAN, angkatan);
        editor.putInt(Constants.KEY_LEVEL, level);
        editor.putString(Constants.KEY_ID_ORMAWA, idOrmawa);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    // FIX FINAL — AMBIL user_id TANPA CLASSCAST
    public String getUserId() {
        Object raw = sharedPreferences.getAll().get(Constants.KEY_USER_ID);

        if (raw instanceof String) {
            return (String) raw;
        }

        if (raw instanceof Integer) {
            return String.valueOf((Integer) raw);
        }

        return null;
    }


    public String getToken() {
        return sharedPreferences.getString(Constants.KEY_TOKEN, "");
    }

    public String getNim() {
        return sharedPreferences.getString(Constants.KEY_NIM, "");
    }

    public String getFullName() {
        return sharedPreferences.getString(Constants.KEY_FULL_NAME, "");
    }

    public String getEmail() {
        return sharedPreferences.getString(Constants.KEY_EMAIL, "");
    }

    public String getProgramStudi() {
        return sharedPreferences.getString(Constants.KEY_PROGRAM_STUDI, "");
    }

    public String getAngkatan() {
        return sharedPreferences.getString(Constants.KEY_ANGKATAN, "");
    }

    public int getLevel() {
        return sharedPreferences.getInt(Constants.KEY_LEVEL, 0);
    }

    public String getIdOrmawa() {
        return sharedPreferences.getString(Constants.KEY_ID_ORMAWA, "");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}

package com.inovarka.myormawa.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String getTimeAgo(String createdAt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            Date date = sdf.parse(createdAt);
            long diff = System.currentTimeMillis() - date.getTime();

            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (seconds < 60) return "Baru saja";
            if (minutes < 60) return minutes + " menit lalu";
            if (hours < 24) return hours + " jam lalu";
            if (days == 1) return "Kemarin";
            if (days < 7) return days + " hari lalu";

            // older than a week → show date
            return createdAt.substring(0, 10);

        } catch (ParseException e) {
            return createdAt;
        }
    }
}

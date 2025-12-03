package com.inovarka.myormawa.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inovarka.myormawa.models.Meeting;
import com.inovarka.myormawa.models.ReminderItem;
import com.inovarka.myormawa.models.ReminderReceiver;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReminderStorage {

    private static final String PREF_NAME = "reminder_pref";
    private static final String KEY_REMINDERS = "reminder_list";

    public static List<ReminderItem> getReminders(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_REMINDERS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<ReminderItem>>(){}.getType();
        return new Gson().fromJson(json, type);
    }

    public static void saveReminders(Context context, List<ReminderItem> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_REMINDERS, new Gson().toJson(list)).apply();
    }

    public static void addReminder(Context context, ReminderItem item) {
        List<ReminderItem> list = getReminders(context);
        // prevent duplicate by meeting id
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMeeting().getId().equals(item.getMeeting().getId())) {
                list.set(i, item); // ✅ ganti item di list
                saveReminders(context, list);
                return;
            }
        }
        list.add(item);
        saveReminders(context, list);
    }

    public static void scheduleReminder(Context context, ReminderItem item) {
        long meetingTime = item.getMeeting().getTimeInMillis();
        long reminderTime = meetingTime - item.getMinutesBefore() * 60 * 1000;

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("meeting_name", item.getMeeting().getNama());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                item.getMeeting().getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
    }


    public static void removeReminder(Context context, String meetingId) {
        List<ReminderItem> list = getReminders(context);
        Iterator<ReminderItem> it = list.iterator();
        while (it.hasNext()) {
            ReminderItem r = it.next();
            if (r.getMeeting().getId().equals(meetingId)) {
                it.remove();
            }
        }
        saveReminders(context, list);
    }

    public static boolean isReminderSet(Context context, String meetingId) {
        List<ReminderItem> list = getReminders(context);
        for (ReminderItem r : list) {
            if (r.getMeeting().getId().equals(meetingId)) return true;
        }
        return false;
    }

    public static ReminderItem getReminderByMeetingId(Context context, String meetingId) {
        List<ReminderItem> list = getReminders(context);
        for (ReminderItem r : list) {
            if (r.getMeeting().getId().equals(meetingId)) return r;
        }
        return null;
    }
}

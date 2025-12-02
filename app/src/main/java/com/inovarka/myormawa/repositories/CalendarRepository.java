package com.inovarka.myormawa.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.models.CalendarEvent;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.network.ApiService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarRepository {
    private static final String TAG = "CalendarRepository";
    private final ApiService apiService;
    private final SimpleDateFormat dateKeyFormat;

    public CalendarRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.dateKeyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    /**
     * Fetch semua calendar events dari API
     */
    public LiveData<List<CalendarEvent>> getCalendarEvents() {
        MutableLiveData<List<CalendarEvent>> eventsLiveData = new MutableLiveData<>();

        Log.d(TAG, "===== FETCHING CALENDAR EVENTS FROM API =====");

        apiService.getCalendarEvents().enqueue(new Callback<ApiResponseList<CalendarEvent>>() {
            @Override
            public void onResponse(Call<ApiResponseList<CalendarEvent>> call, Response<ApiResponseList<CalendarEvent>> response) {
                Log.d(TAG, "API Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        List<CalendarEvent> events = response.body().getData();

                        if (events != null && !events.isEmpty()) {
                            Log.d(TAG, "Calendar events loaded: " + events.size());
                            eventsLiveData.postValue(events);
                        } else {
                            Log.w(TAG, "Calendar events list is empty");
                            eventsLiveData.postValue(new ArrayList<>());
                        }
                    } else {
                        Log.e(TAG, "API returned unsuccessful: " + response.body().getMessage());
                        eventsLiveData.postValue(new ArrayList<>());
                    }
                } else {
                    Log.e(TAG, "Response unsuccessful: " + response.code());
                    eventsLiveData.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<CalendarEvent>> call, Throwable t) {
                Log.e(TAG, "===== API CALL FAILED =====");
                Log.e(TAG, "Error: " + t.getMessage(), t);
                eventsLiveData.postValue(new ArrayList<>());
            }
        });

        return eventsLiveData;
    }

    /**
     * Mendapatkan Map tanggal yang punya event
     * Key: "yyyy-MM-dd"
     * Value: List<CalendarEvent>
     *
     * PENTING: Jika event multi-hari (28-29 Des), semua tanggal dalam range akan punya indicator
     */
    public Map<String, List<CalendarEvent>> getEventsByDate(List<CalendarEvent> events) {
        Map<String, List<CalendarEvent>> eventsMap = new HashMap<>();

        for (CalendarEvent event : events) {
            try {
                Date startDate = dateKeyFormat.parse(event.getTglMulai());
                Date endDate = dateKeyFormat.parse(event.getTglSelesai());

                if (startDate != null && endDate != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDate);

                    // Loop semua tanggal dalam range (inclusive)
                    while (!calendar.getTime().after(endDate)) {
                        String dateKey = dateKeyFormat.format(calendar.getTime());

                        if (!eventsMap.containsKey(dateKey)) {
                            eventsMap.put(dateKey, new ArrayList<>());
                        }
                        eventsMap.get(dateKey).add(event);

                        Log.d(TAG, "Event '" + event.getTitle() + "' added to date: " + dateKey);

                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date for event: " + event.getTitle(), e);
            }
        }

        return eventsMap;
    }
}
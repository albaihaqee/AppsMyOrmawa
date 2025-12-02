package com.inovarka.myormawa.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.models.Event;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.network.ApiService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventRepository {
    private static final String TAG = "EventRepository";
    private final ApiService apiService;

    public EventRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    /**
     * Fetch semua event yang aktif (upcoming)
     */
    public LiveData<List<Event>> getUpcomingEvents() {
        MutableLiveData<List<Event>> eventsLiveData = new MutableLiveData<>();

        Log.d(TAG, "===== FETCHING EVENTS FROM API =====");

        apiService.getAllEvents().enqueue(new Callback<ApiResponseList<Event>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Event>> call, Response<ApiResponseList<Event>> response) {
                Log.d(TAG, "API Response Code: " + response.code());
                Log.d(TAG, "API Response URL: " + call.request().url());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response body is not null");
                    Log.d(TAG, "Success: " + response.body().isSuccess());
                    Log.d(TAG, "Message: " + response.body().getMessage());

                    if (response.body().isSuccess()) {
                        List<Event> events = response.body().getData();

                        if (events != null && !events.isEmpty()) {
                            Log.d(TAG, "Raw events from API: " + events.size());

                            // Log detail setiap event dari API
                            for (int i = 0; i < events.size(); i++) {
                                Event e = events.get(i);
                                Log.d(TAG, "Raw Event #" + (i+1) + ": " + e.getTitle());
                                Log.d(TAG, "  Poster: " + e.getPosterUrl());
                                Log.d(TAG, "  GuideBook: " + e.getGuideBookUrl());
                                Log.d(TAG, "  Tgl Selesai: " + e.getTglSelesai());
                            }

                            List<Event> upcomingEvents = filterUpcomingEvents(events);

                            Log.d(TAG, "Filtered upcoming events: " + upcomingEvents.size());

                            eventsLiveData.postValue(upcomingEvents);
                        } else {
                            Log.w(TAG, "Events list is null or empty");
                            eventsLiveData.postValue(new ArrayList<Event>());
                        }
                    } else {
                        Log.e(TAG, "API returned unsuccessful: " + response.body().getMessage());
                        eventsLiveData.postValue(new ArrayList<Event>());
                    }
                } else {
                    Log.e(TAG, "Response unsuccessful or body is null");
                    Log.e(TAG, "Response code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    eventsLiveData.postValue(new ArrayList<Event>());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<Event>> call, Throwable t) {
                Log.e(TAG, "===== API CALL FAILED =====");
                Log.e(TAG, "Error message: " + t.getMessage());
                Log.e(TAG, "Error class: " + t.getClass().getSimpleName());
                Log.e(TAG, "URL attempted: " + call.request().url());
                t.printStackTrace();
                eventsLiveData.postValue(new ArrayList<Event>());
            }
        });

        return eventsLiveData;
    }

    /**
     * Fetch event terbaru (max 4 items, sorted by ID desc)
     */
    public LiveData<List<Event>> getRecentEvents(int maxCount) {
        MutableLiveData<List<Event>> eventsLiveData = new MutableLiveData<>();

        Log.d(TAG, "Fetching recent events (max: " + maxCount + ")");

        apiService.getAllEvents().enqueue(new Callback<ApiResponseList<Event>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Event>> call, Response<ApiResponseList<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        List<Event> events = response.body().getData();
                        if (events != null && !events.isEmpty()) {
                            List<Event> upcomingEvents = filterUpcomingEvents(events);

                            // Sort by ID descending
                            Collections.sort(upcomingEvents, new Comparator<Event>() {
                                @Override
                                public int compare(Event e1, Event e2) {
                                    try {
                                        int id1 = Integer.parseInt(e1.getId());
                                        int id2 = Integer.parseInt(e2.getId());
                                        return Integer.compare(id2, id1);
                                    } catch (NumberFormatException e) {
                                        return 0;
                                    }
                                }
                            });

                            // Get max N events
                            int size = Math.min(maxCount, upcomingEvents.size());
                            List<Event> recentEvents = upcomingEvents.subList(0, size);

                            Log.d(TAG, "Returning " + recentEvents.size() + " recent events");
                            eventsLiveData.postValue(recentEvents);
                        } else {
                            eventsLiveData.postValue(new ArrayList<Event>());
                        }
                    } else {
                        Log.e(TAG, "API returned unsuccessful: " + response.body().getMessage());
                        eventsLiveData.postValue(new ArrayList<Event>());
                    }
                } else {
                    Log.e(TAG, "Response unsuccessful: " + response.code());
                    eventsLiveData.postValue(new ArrayList<Event>());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<Event>> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                eventsLiveData.postValue(new ArrayList<Event>());
            }
        });

        return eventsLiveData;
    }

    /**
     * Filter events yang masih upcoming (tanggal selesai >= hari ini)
     */
    private List<Event> filterUpcomingEvents(List<Event> events) {
        List<Event> upcomingEvents = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = new Date();

        Log.d(TAG, "Filtering events. Today: " + sdf.format(today));

        for (Event event : events) {
            try {
                String tglSelesai = event.getTglSelesai();
                if (tglSelesai != null && !tglSelesai.isEmpty()) {
                    Date endDate = sdf.parse(tglSelesai);
                    if (endDate != null) {
                        if (!endDate.before(today)) {
                            upcomingEvents.add(event);
                            Log.d(TAG, "✓ Event included: " + event.getTitle() + " (ends: " + tglSelesai + ")");
                        } else {
                            Log.d(TAG, "✗ Event excluded (past): " + event.getTitle() + " (ended: " + tglSelesai + ")");
                        }
                    }
                } else {
                    Log.w(TAG, "? Event has no end date: " + event.getTitle());
                    upcomingEvents.add(event);
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date for event: " + event.getTitle(), e);
                upcomingEvents.add(event);
            }
        }

        return upcomingEvents;
    }
}
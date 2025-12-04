package com.inovarka.myormawa.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.models.Scholarship;
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

public class ScholarshipRepository {
    private static final String TAG = "ScholarshipRepository";
    private final ApiService apiService;

    public ScholarshipRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    /**
     * Fetch semua scholarship yang aktif (upcoming)
     */
    public LiveData<List<Scholarship>> getUpcomingScholarships() {
        MutableLiveData<List<Scholarship>> scholarshipsLiveData = new MutableLiveData<>();

        Log.d(TAG, "===== FETCHING SCHOLARSHIPS FROM API =====");

        apiService.getAllScholarships().enqueue(new Callback<ApiResponseList<Scholarship>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Scholarship>> call, Response<ApiResponseList<Scholarship>> response) {
                Log.d(TAG, "API Response Code: " + response.code());
                Log.d(TAG, "API Response URL: " + call.request().url());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response body is not null");
                    Log.d(TAG, "Success: " + response.body().isSuccess());
                    Log.d(TAG, "Message: " + response.body().getMessage());

                    if (response.body().isSuccess()) {
                        List<Scholarship> scholarships = response.body().getData();

                        if (scholarships != null && !scholarships.isEmpty()) {
                            Log.d(TAG, "Raw scholarships from API: " + scholarships.size());

                            // Log detail setiap scholarship dari API
                            for (int i = 0; i < scholarships.size(); i++) {
                                Scholarship s = scholarships.get(i);
                                Log.d(TAG, "Raw Scholarship #" + (i+1) + ": " + s.getTitle());
                                Log.d(TAG, "  Poster: " + s.getPosterUrl());
                                Log.d(TAG, "  GuideBook: " + s.getGuideBookUrl());
                                Log.d(TAG, "  Deadline Raw: " + s.getDeadlineRaw());
                            }

                            List<Scholarship> upcomingScholarships = filterUpcomingScholarships(scholarships);

                            Log.d(TAG, "Filtered upcoming scholarships: " + upcomingScholarships.size());

                            scholarshipsLiveData.postValue(upcomingScholarships);
                        } else {
                            Log.w(TAG, "Scholarships list is null or empty");
                            scholarshipsLiveData.postValue(new ArrayList<Scholarship>());
                        }
                    } else {
                        Log.e(TAG, "API returned unsuccessful: " + response.body().getMessage());
                        scholarshipsLiveData.postValue(new ArrayList<Scholarship>());
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
                    scholarshipsLiveData.postValue(new ArrayList<Scholarship>());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<Scholarship>> call, Throwable t) {
                Log.e(TAG, "===== API CALL FAILED =====");
                Log.e(TAG, "Error message: " + t.getMessage());
                Log.e(TAG, "Error class: " + t.getClass().getSimpleName());
                Log.e(TAG, "URL attempted: " + call.request().url());
                t.printStackTrace();
                scholarshipsLiveData.postValue(new ArrayList<Scholarship>());
            }
        });

        return scholarshipsLiveData;
    }

    /**
     * Fetch scholarship terbaru (max items, sorted by ID desc)
     */
    public LiveData<List<Scholarship>> getRecentScholarships(int maxCount) {
        MutableLiveData<List<Scholarship>> scholarshipsLiveData = new MutableLiveData<>();

        Log.d(TAG, "Fetching recent scholarships (max: " + maxCount + ")");

        apiService.getAllScholarships().enqueue(new Callback<ApiResponseList<Scholarship>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Scholarship>> call, Response<ApiResponseList<Scholarship>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        List<Scholarship> scholarships = response.body().getData();
                        if (scholarships != null && !scholarships.isEmpty()) {
                            List<Scholarship> upcomingScholarships = filterUpcomingScholarships(scholarships);

                            // Sort by ID descending
                            Collections.sort(upcomingScholarships, new Comparator<Scholarship>() {
                                @Override
                                public int compare(Scholarship s1, Scholarship s2) {
                                    try {
                                        int id1 = Integer.parseInt(s1.getId());
                                        int id2 = Integer.parseInt(s2.getId());
                                        return Integer.compare(id2, id1);
                                    } catch (NumberFormatException e) {
                                        return 0;
                                    }
                                }
                            });

                            // Get max N scholarships
                            int size = Math.min(maxCount, upcomingScholarships.size());
                            List<Scholarship> recentScholarships = upcomingScholarships.subList(0, size);

                            Log.d(TAG, "Returning " + recentScholarships.size() + " recent scholarships");
                            scholarshipsLiveData.postValue(recentScholarships);
                        } else {
                            scholarshipsLiveData.postValue(new ArrayList<Scholarship>());
                        }
                    } else {
                        Log.e(TAG, "API returned unsuccessful: " + response.body().getMessage());
                        scholarshipsLiveData.postValue(new ArrayList<Scholarship>());
                    }
                } else {
                    Log.e(TAG, "Response unsuccessful: " + response.code());
                    scholarshipsLiveData.postValue(new ArrayList<Scholarship>());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<Scholarship>> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                scholarshipsLiveData.postValue(new ArrayList<Scholarship>());
            }
        });

        return scholarshipsLiveData;
    }

    /**
     * Filter scholarships yang masih upcoming (deadline >= hari ini)
     */
    private List<Scholarship> filterUpcomingScholarships(List<Scholarship> scholarships) {
        List<Scholarship> upcomingScholarships = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = new Date();

        Log.d(TAG, "Filtering scholarships. Today: " + sdf.format(today));

        for (Scholarship scholarship : scholarships) {
            try {
                String deadlineRaw = scholarship.getDeadlineRaw();
                if (deadlineRaw != null && !deadlineRaw.isEmpty()) {
                    Date deadlineDate = sdf.parse(deadlineRaw);
                    if (deadlineDate != null) {
                        if (!deadlineDate.before(today)) {
                            upcomingScholarships.add(scholarship);
                            Log.d(TAG, "✓ Scholarship included: " + scholarship.getTitle() + " (deadline: " + deadlineRaw + ")");
                        } else {
                            Log.d(TAG, "✗ Scholarship excluded (past): " + scholarship.getTitle() + " (deadline: " + deadlineRaw + ")");
                        }
                    }
                } else {
                    Log.w(TAG, "? Scholarship has no deadline: " + scholarship.getTitle());
                    upcomingScholarships.add(scholarship);
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date for scholarship: " + scholarship.getTitle(), e);
                upcomingScholarships.add(scholarship);
            }
        }

        return upcomingScholarships;
    }
}
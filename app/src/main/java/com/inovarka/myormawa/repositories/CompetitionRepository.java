package com.inovarka.myormawa.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.models.Competition;
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

public class CompetitionRepository {
    private static final String TAG = "CompetitionRepository";
    private final ApiService apiService;

    public CompetitionRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    /**
     * Fetch semua competition yang aktif (upcoming)
     */
    public LiveData<List<Competition>> getUpcomingCompetitions() {
        MutableLiveData<List<Competition>> competitionsLiveData = new MutableLiveData<>();

        Log.d(TAG, "===== FETCHING COMPETITIONS FROM API =====");

        apiService.getAllCompetitions().enqueue(new Callback<ApiResponseList<Competition>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Competition>> call, Response<ApiResponseList<Competition>> response) {
                Log.d(TAG, "API Response Code: " + response.code());
                Log.d(TAG, "API Response URL: " + call.request().url());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response body is not null");
                    Log.d(TAG, "Success: " + response.body().isSuccess());
                    Log.d(TAG, "Message: " + response.body().getMessage());

                    if (response.body().isSuccess()) {
                        List<Competition> competitions = response.body().getData();

                        if (competitions != null && !competitions.isEmpty()) {
                            Log.d(TAG, "Raw competitions from API: " + competitions.size());

                            // Log detail setiap competition dari API
                            for (int i = 0; i < competitions.size(); i++) {
                                Competition c = competitions.get(i);
                                Log.d(TAG, "Raw Competition #" + (i+1) + ": " + c.getTitle());
                                Log.d(TAG, "  Poster: " + c.getPosterUrl());
                                Log.d(TAG, "  GuideBook: " + c.getGuideBookUrl());
                                Log.d(TAG, "  Tgl Selesai: " + c.getTglSelesai());
                            }

                            List<Competition> upcomingCompetitions = filterUpcomingCompetitions(competitions);

                            Log.d(TAG, "Filtered upcoming competitions: " + upcomingCompetitions.size());

                            competitionsLiveData.postValue(upcomingCompetitions);
                        } else {
                            Log.w(TAG, "Competitions list is null or empty");
                            competitionsLiveData.postValue(new ArrayList<Competition>());
                        }
                    } else {
                        Log.e(TAG, "API returned unsuccessful: " + response.body().getMessage());
                        competitionsLiveData.postValue(new ArrayList<Competition>());
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
                    competitionsLiveData.postValue(new ArrayList<Competition>());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<Competition>> call, Throwable t) {
                Log.e(TAG, "===== API CALL FAILED =====");
                Log.e(TAG, "Error message: " + t.getMessage());
                Log.e(TAG, "Error class: " + t.getClass().getSimpleName());
                Log.e(TAG, "URL attempted: " + call.request().url());
                t.printStackTrace();
                competitionsLiveData.postValue(new ArrayList<Competition>());
            }
        });

        return competitionsLiveData;
    }

    /**
     * Fetch competition terbaru (max items, sorted by ID desc)
     */
    public LiveData<List<Competition>> getRecentCompetitions(int maxCount) {
        MutableLiveData<List<Competition>> competitionsLiveData = new MutableLiveData<>();

        Log.d(TAG, "Fetching recent competitions (max: " + maxCount + ")");

        apiService.getAllCompetitions().enqueue(new Callback<ApiResponseList<Competition>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Competition>> call, Response<ApiResponseList<Competition>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        List<Competition> competitions = response.body().getData();
                        if (competitions != null && !competitions.isEmpty()) {
                            List<Competition> upcomingCompetitions = filterUpcomingCompetitions(competitions);

                            // Sort by ID descending
                            Collections.sort(upcomingCompetitions, new Comparator<Competition>() {
                                @Override
                                public int compare(Competition c1, Competition c2) {
                                    try {
                                        int id1 = Integer.parseInt(c1.getId());
                                        int id2 = Integer.parseInt(c2.getId());
                                        return Integer.compare(id2, id1);
                                    } catch (NumberFormatException e) {
                                        return 0;
                                    }
                                }
                            });

                            // Get max N competitions
                            int size = Math.min(maxCount, upcomingCompetitions.size());
                            List<Competition> recentCompetitions = upcomingCompetitions.subList(0, size);

                            Log.d(TAG, "Returning " + recentCompetitions.size() + " recent competitions");
                            competitionsLiveData.postValue(recentCompetitions);
                        } else {
                            competitionsLiveData.postValue(new ArrayList<Competition>());
                        }
                    } else {
                        Log.e(TAG, "API returned unsuccessful: " + response.body().getMessage());
                        competitionsLiveData.postValue(new ArrayList<Competition>());
                    }
                } else {
                    Log.e(TAG, "Response unsuccessful: " + response.code());
                    competitionsLiveData.postValue(new ArrayList<Competition>());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<Competition>> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                competitionsLiveData.postValue(new ArrayList<Competition>());
            }
        });

        return competitionsLiveData;
    }

    /**
     * Filter competitions yang masih upcoming (tanggal selesai >= hari ini)
     */
    private List<Competition> filterUpcomingCompetitions(List<Competition> competitions) {
        List<Competition> upcomingCompetitions = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = new Date();

        Log.d(TAG, "Filtering competitions. Today: " + sdf.format(today));

        for (Competition competition : competitions) {
            try {
                String tglSelesai = competition.getTglSelesai();
                if (tglSelesai != null && !tglSelesai.isEmpty()) {
                    Date endDate = sdf.parse(tglSelesai);
                    if (endDate != null) {
                        if (!endDate.before(today)) {
                            upcomingCompetitions.add(competition);
                            Log.d(TAG, "✓ Competition included: " + competition.getTitle() + " (ends: " + tglSelesai + ")");
                        } else {
                            Log.d(TAG, "✗ Competition excluded (past): " + competition.getTitle() + " (ended: " + tglSelesai + ")");
                        }
                    }
                } else {
                    Log.w(TAG, "? Competition has no end date: " + competition.getTitle());
                    upcomingCompetitions.add(competition);
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date for competition: " + competition.getTitle(), e);
                upcomingCompetitions.add(competition);
            }
        }

        return upcomingCompetitions;
    }
}
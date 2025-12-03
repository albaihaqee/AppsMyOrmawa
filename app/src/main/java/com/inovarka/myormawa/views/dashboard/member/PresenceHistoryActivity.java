package com.inovarka.myormawa.views.dashboard.member;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.PresenceHistoryAdapter;
import com.inovarka.myormawa.models.AttendanceData;
import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenceHistoryActivity extends AppCompatActivity {

    private RecyclerView rvPresence;
    private PresenceHistoryAdapter presenceAdapter;
    private List<AttendanceData> attendanceList;
    private LinearLayout layoutEmptyState;
    private ProgressBar progressBar;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presence_history);

        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadPresenceData();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        rvPresence = findViewById(R.id.rv_presence);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        progressBar = findViewById(R.id.progress_bar);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        attendanceList = new ArrayList<>();
        presenceAdapter = new PresenceHistoryAdapter(attendanceList);
        rvPresence.setLayoutManager(new LinearLayoutManager(this));
        rvPresence.setAdapter(presenceAdapter);
    }

    private void loadPresenceData() {
        showLoading(true);

        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User tidak teridentifikasi", Toast.LENGTH_LONG).show();
            showLoading(false);
            updateEmptyState();
            return;
        }

        ApiClient.getApiService().getUserAttendanceHistory("get_history", userId)
                .enqueue(new Callback<ApiResponseList<AttendanceData>>() {
                    @Override
                    public void onResponse(Call<ApiResponseList<AttendanceData>> call, Response<ApiResponseList<AttendanceData>> response) {
                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            attendanceList.clear();
                            List<AttendanceData> dataList = response.body().getData();
                            if (dataList != null && !dataList.isEmpty()) {
                                for (AttendanceData data : dataList) {
                                    attendanceList.add(data);
                                }
                                presenceAdapter.notifyDataSetChanged();
                            }
                            updateEmptyState();
                        } else {
                            Toast.makeText(PresenceHistoryActivity.this, "Gagal memuat data", Toast.LENGTH_LONG).show();
                            updateEmptyState();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponseList<AttendanceData>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(PresenceHistoryActivity.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("PresenceHistory", "API Failure: " + t.getMessage(), t);
                        updateEmptyState();
                    }
                });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvPresence.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateEmptyState() {
        if (attendanceList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvPresence.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvPresence.setVisibility(View.VISIBLE);
        }
    }
}

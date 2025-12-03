package com.inovarka.myormawa.views.dashboard.member;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.PresenceHistoryAdapter;
import com.inovarka.myormawa.models.PresenceHistory;

import java.util.ArrayList;
import java.util.List;

public class PresenceHistoryActivity extends AppCompatActivity {

    private RecyclerView rvPresence;
    private PresenceHistoryAdapter presenceAdapter;
    private List<PresenceHistory> presenceList;
    private LinearLayout layoutEmptyState;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presence_history);

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
        presenceList = new ArrayList<>();
        presenceAdapter = new PresenceHistoryAdapter(presenceList);
        rvPresence.setLayoutManager(new LinearLayoutManager(this));
        rvPresence.setAdapter(presenceAdapter);
    }

    private void loadPresenceData() {
        showLoading(true);

        rvPresence.postDelayed(() -> {
            presenceList.add(new PresenceHistory(
                    "1",
                    "Presensi Rapat Evaluasi Bulanan",
                    "Senin, 25 November 2024",
                    "08:00",   // mulai
                    "09:00",   // berakhir
                    "09:35"    // user absen → terlambat
            ));

            presenceList.add(new PresenceHistory(
                    "2",
                    "Presensi Kegiatan Pelatihan",
                    "Selasa, 26 November 2024",
                    "13:00",
                    "14:00",
                    "13:45"  // tidak terlambat
            ));


            presenceAdapter.notifyDataSetChanged();
            showLoading(false);
            updateEmptyState();
        }, 1000);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvPresence.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateEmptyState() {
        if (presenceList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvPresence.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvPresence.setVisibility(View.VISIBLE);
        }
    }
}
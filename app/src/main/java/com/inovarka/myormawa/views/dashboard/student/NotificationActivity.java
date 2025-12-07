package com.inovarka.myormawa.views.dashboard.student;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.NotificationAdapter;
import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.models.Competition;
import com.inovarka.myormawa.models.Event;
import com.inovarka.myormawa.models.Notification;
import com.inovarka.myormawa.models.Scholarship;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private ChipGroup chipGroup;

    private List<Notification> allNotifications = new ArrayList<>();
    private List<Notification> filteredNotifications = new ArrayList<>();

    private ApiService apiService;
    private String currentCategory = "Semua";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_notification);

        apiService = ApiClient.getClient().create(ApiService.class);

        initViews();
        setupRecyclerView();
        setupChips();
        loadCompetitionNotifications();
        loadScholarshipNotifications();
        loadEventNotifications();// <-- Tambahkan ini
    }

    private void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        rvNotifications = findViewById(R.id.rv_notifications);
        chipGroup = findViewById(R.id.chip_group_notification);
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter();
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);

        adapter.setOnItemClickListener(notification -> {
            notification.setRead(true);
            adapter.notifyDataSetChanged();
        });
    }

    private void setupChips() {
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int selectedChipId = checkedIds.get(0);
                Chip chip = findViewById(selectedChipId);
                filterNotifications(chip.getText().toString());
            }
        });
    }

    private void filterNotifications(String category) {
        currentCategory = category;
        filteredNotifications.clear();

        if (category.equals("Semua")) {
            filteredNotifications.addAll(allNotifications);
        } else {
            for (Notification notif : allNotifications) {
                if (notif.getCategory().equalsIgnoreCase(category)) {
                    filteredNotifications.add(notif);
                }
            }
        }

        adapter.setNotifications(filteredNotifications);
    }

    // ============================================
    //   LOAD NOTIFICATION FROM COMPETITION API
    // ============================================
    private void loadCompetitionNotifications() {

        apiService.getAllCompetitions().enqueue(new Callback<ApiResponseList<Competition>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Competition>> call,
                                   Response<ApiResponseList<Competition>> response) {

                if (response.body() == null || !response.body().isSuccess()) {
                    Toast.makeText(NotificationActivity.this,
                            "Gagal memuat notifikasi kompetisi",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Competition> competitions = response.body().getData();
                allNotifications.clear();

                for (Competition c : competitions) {

                    Notification notif = new Notification(
                            c.getId(),
                            c.getTitle(),
                            c.getDescription(),
                            "Kompetisi",
                            c.getCreatedAt(),
                            false
                    );

                    notif.setTime(timeAgo(c.getCreatedAt()));
                    allNotifications.add(notif);
                }

                filterNotifications(currentCategory);
            }

            @Override
            public void onFailure(Call<ApiResponseList<Competition>> call, Throwable t) {
                Toast.makeText(NotificationActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadScholarshipNotifications() {

        apiService.getAllScholarships().enqueue(new Callback<ApiResponseList<Scholarship>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Scholarship>> call,
                                   Response<ApiResponseList<Scholarship>> response) {

                if (response.body() == null || !response.body().isSuccess()) {
                    Toast.makeText(NotificationActivity.this,
                            "Gagal memuat notifikasi beasiswa",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Scholarship> list = response.body().getData();

                for (Scholarship s : list) {

                    Notification notif = Notification.fromScholarship(
                            s.getId(),
                            s.getTitle(),
                            s.getProvider(),
                            s.getDescription(),
                            s.getCreatedAt()
                    );

                    notif.setTime(timeAgo(s.getCreatedAt()));
                    allNotifications.add(notif);
                }

                // Refresh berdasar filter chip
                filterNotifications(currentCategory);
            }

            @Override
            public void onFailure(Call<ApiResponseList<Scholarship>> call, Throwable t) {
                Toast.makeText(NotificationActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEventNotifications() {

        apiService.getAllEvents().enqueue(new Callback<ApiResponseList<Event>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Event>> call,
                                   Response<ApiResponseList<Event>> response) {

                if (response.body() == null || !response.body().isSuccess()) {
                    Toast.makeText(NotificationActivity.this,
                            "Gagal memuat notifikasi event",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Event> list = response.body().getData();

                for (Event e : list) {

                    Notification notif = new Notification(
                            e.getId(),
                            e.getTitle(),
                            e.getDescription(),
                            "Event",
                            e.getCreatedAt(),
                            false
                    );

                    notif.setTime(timeAgo(e.getCreatedAt()));
                    allNotifications.add(notif);
                }

                // Refresh berdasar filter chip
                filterNotifications(currentCategory);
            }

            @Override
            public void onFailure(Call<ApiResponseList<Event>> call, Throwable t) {
                Toast.makeText(NotificationActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private String timeAgo(String createdAt) {
        try {
            // format tanggal dari API: "2025-01-05 12:00:00"
            java.text.SimpleDateFormat sdf =
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());

            long time = sdf.parse(createdAt).getTime();
            long now = System.currentTimeMillis();
            long diff = now - time;

            long minutes = diff / 60000;
            long hours = diff / 3600000;
            long days = diff / 86400000;

            if (minutes < 1) return "Baru saja";
            if (minutes < 60) return minutes + " menit lalu";
            if (hours < 24) return hours + " jam lalu";
            if (days == 1) return "Kemarin";
            if (days < 7) return days + " hari lalu";

            // Kalau lebih dari seminggu → tampilkan tanggal asli
            java.text.SimpleDateFormat output =
                    new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());
            return output.format(sdf.parse(createdAt));
        }
        catch (Exception e) {
            return createdAt;
        }
    }

}

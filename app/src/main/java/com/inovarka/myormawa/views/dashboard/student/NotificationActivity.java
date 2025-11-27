package com.inovarka.myormawa.views.dashboard.student;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.NotificationAdapter;
import com.inovarka.myormawa.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private ChipGroup chipGroup;
    private List<Notification> allNotifications;
    private List<Notification> filteredNotifications;

    private String currentCategory = "Semua";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_notification);

        initViews();
        setupRecyclerView();
        setupChips();
        loadDummyData();
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
                Chip selectedChip = findViewById(selectedChipId);
                String category = selectedChip.getText().toString();
                filterNotifications(category);
            }
        });
    }

    private void filterNotifications(String category) {
        currentCategory = category;
        filteredNotifications = new ArrayList<>();

        if (category.equals("Semua")) {
            filteredNotifications.addAll(allNotifications);
        } else {
            for (Notification notif : allNotifications) {
                if (notif.getCategory().equals(category)) {
                    filteredNotifications.add(notif);
                }
            }
        }
        adapter.setNotifications(filteredNotifications);
    }

    private void loadDummyData() {
        allNotifications = new ArrayList<>();
        allNotifications.add(new Notification("1", "Pendaftaran Oprec HMJ-TI Dibuka",
                "Segera daftarkan dirimu!", "Info", "2 jam yang lalu", false));
        allNotifications.add(new Notification("2", "Reminder: Workshop UI/UX",
                "Workshop akan dimulai besok pukul 13:00", "Event", "5 jam yang lalu", false));
        allNotifications.add(new Notification("3", "Beasiswa PPA 2025",
                "Deadline pendaftaran 30 November 2025", "Beasiswa", "1 hari yang lalu", true));
        allNotifications.add(new Notification("4", "Hackathon AI Innovation",
                "Pendaftaran akan segera ditutup", "Kompetisi", "2 hari yang lalu", true));
        allNotifications.add(new Notification("5", "Update Sistem Aplikasi",
                "Fitur baru telah ditambahkan", "Info", "3 hari yang lalu", true));

        filterNotifications("Semua");
    }
}
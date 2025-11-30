package com.inovarka.myormawa.views.dashboard.member;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.NotificationMemberAdapter;
import com.inovarka.myormawa.models.NotificationMember;

import java.util.ArrayList;
import java.util.List;

public class NotificationMemberActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationMemberAdapter adapter;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_member);

        recyclerView = findViewById(R.id.rv_notifications);
        btnBack = findViewById(R.id.btn_back);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationMemberAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setItems(getDummyNotifications());

        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private List<NotificationMember> getDummyNotifications() {
        List<NotificationMember> list = new ArrayList<>();

        list.add(new NotificationMember(
                "1",
                "Rapat Koordinasi HMJ",
                "Evaluasi program kerja & pembahasan agenda baru",
                "25 Nov 2024",
                "09:00 - 11:00",
                "Ruang Meeting Lantai 3",
                "Dikirim 2 jam lalu"
        ));

        list.add(new NotificationMember(
                "2",
                "Meeting Panitia Event",
                "Finalisasi rundown & pembagian tugas",
                "30 Nov 2024",
                "13:00 - 15:00",
                "Aula Gedung TI",
                "Dikirim kemarin"
        ));

        return list;
    }
}

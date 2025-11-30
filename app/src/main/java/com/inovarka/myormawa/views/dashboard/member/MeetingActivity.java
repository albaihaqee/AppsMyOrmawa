package com.inovarka.myormawa.views.dashboard.member;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.MeetingAdapter;
import com.inovarka.myormawa.models.Meeting;
import com.inovarka.myormawa.models.ReminderItem;
import com.inovarka.myormawa.utils.ReminderStorage;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MeetingActivity extends AppCompatActivity {

    private RecyclerView rvMeetings;
    private MeetingAdapter meetingAdapter;
    private TextView chipAll;
    private List<Meeting> fullMeetingList = new ArrayList<>();
    private List<Meeting> displayMeetingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        ImageView btnBack = findViewById(R.id.btn_back);
        TextView tvTitle = findViewById(R.id.tv_title);
        rvMeetings = findViewById(R.id.rv_meetings);
        chipAll = findViewById(R.id.chip_all);

        btnBack.setOnClickListener(v -> finish());
        tvTitle.setText("Riwayat Kegiatan");

        setupRecyclerView();
        loadMeetingData();
        setupChipListeners();
    }

    private void setupRecyclerView() {
        meetingAdapter = new MeetingAdapter(this, displayMeetingList);
        rvMeetings.setLayoutManager(new LinearLayoutManager(this));
        rvMeetings.setAdapter(meetingAdapter);

        meetingAdapter.setOnReminderClick((meeting, bellView) -> {
            // tampilkan dialog pilihan 10/30/60 / batal / hapus
            showReminderOptions(meeting);
        });
    }

    private void showReminderOptions(Meeting meeting) {
        String[] options = new String[] { "10 menit sebelum", "30 menit sebelum", "1 jam sebelum", "Hapus Reminder", "Batal" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Reminder");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) { // 10 min
                ReminderStorage.addReminder(this, new ReminderItem(meeting, 10));
                Toast.makeText(this, "Reminder 10 menit sebelum disimpan", Toast.LENGTH_SHORT).show();
            } else if (which == 1) { // 30 min
                ReminderStorage.addReminder(this, new ReminderItem(meeting, 30));
                Toast.makeText(this, "Reminder 30 menit sebelum disimpan", Toast.LENGTH_SHORT).show();
            } else if (which == 2) { // 60 min
                ReminderStorage.addReminder(this, new ReminderItem(meeting, 60));
                Toast.makeText(this, "Reminder 1 jam sebelum disimpan", Toast.LENGTH_SHORT).show();
            } else if (which == 3) { // hapus
                ReminderStorage.removeReminder(this, meeting.getId());
                Toast.makeText(this, "Reminder dihapus", Toast.LENGTH_SHORT).show();
            } else {
                // cancel
            }
            // refresh adapter untuk update icon bell
            meetingAdapter.notifyDataSetChanged();
        });
        builder.show();
    }

    // (rest of your code: loadMeetingData, filter functions)
    private void setupChipListeners() {
        chipAll.setOnClickListener(v -> {
            meetingAdapter.updateList(fullMeetingList);
        });
    }

    private void loadMeetingData() {
        fullMeetingList.clear();

        // Dummy data
        fullMeetingList.add(new Meeting("1", "Rapat Koordinasi Tim",
                "Evaluasi proyek Q4 dan perencanaan strategi 2025",
                "25 Nov 2024", "09:00", "11:00", "Ruang Meeting Lantai 3"));

        fullMeetingList.add(new Meeting("2", "Workshop Android Development",
                "Pelatihan pembuatan aplikasi mobile dengan Android Studio",
                "26 Nov 2024", "13:00", "16:00", "Lab Komputer A"));

        fullMeetingList.add(new Meeting("3", "Seminar Teknologi AI",
                "Pengenalan dan implementasi AI dalam pengembangan software",
                "27 Nov 2024", "10:00", "12:00", "Auditorium Utama"));

        fullMeetingList.add(new Meeting("4", "Briefing Project Baru",
                "Diskusi requirement dan timeline project client",
                "28 Nov 2024", "14:00", "15:30", "Ruang Meeting Lantai 2"));

        displayMeetingList.clear();
        displayMeetingList.addAll(fullMeetingList);

        meetingAdapter.notifyDataSetChanged();
    }
}

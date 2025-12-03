package com.inovarka.myormawa.views.dashboard.member;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.MeetingAdapter;
import com.inovarka.myormawa.models.Meeting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeetingActivity extends AppCompatActivity {

    private RecyclerView rvMeetings;
    private MeetingAdapter meetingAdapter;
    private TextView chipAll;

    // LIST ASLI (TIDAK BOLEH DIHAPUS)
    private List<Meeting> fullMeetingList = new ArrayList<>();

    // LIST YANG DITAMPILKAN DI ADAPTER
    private List<Meeting> displayMeetingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        // Initialize views
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

    private void setupChipListeners() {

        chipAll.setOnClickListener(v -> {
            setActiveChip(chipAll);
            meetingAdapter.updateList(fullMeetingList);  // tampilkan semua data
        });

    }

    private void setActiveChip(TextView activeChip) {
        TextView[] chips = {chipAll};
    }

    // FILTER BERDASARKAN BULAN
    private void filterByMonth(String type) {

        List<Meeting> filtered = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        Calendar now = Calendar.getInstance();

        int currentMonth = now.get(Calendar.MONTH);
        int currentYear = now.get(Calendar.YEAR);

        for (Meeting m : fullMeetingList) {
            try {
                Date date = sdf.parse(m.getDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

                // Bulan ini
                if (type.equals("this")) {
                    if (month == currentMonth && year == currentYear) {
                        filtered.add(m);
                    }
                }

                // Bulan lalu
                if (type.equals("last")) {
                    int lastMonth = currentMonth - 1;
                    int lastYear = currentYear;

                    if (lastMonth < 0) { // Januari → Desember tahun sebelumnya
                        lastMonth = 11;
                        lastYear -= 1;
                    }

                    if (month == lastMonth && year == lastYear) {
                        filtered.add(m);
                    }
                }

            } catch (Exception ignored) {}
        }

        meetingAdapter.updateList(filtered);
    }

    private void setupRecyclerView() {
        meetingAdapter = new MeetingAdapter(displayMeetingList);
        rvMeetings.setLayoutManager(new LinearLayoutManager(this));
        rvMeetings.setAdapter(meetingAdapter);
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

        // copy ke display list
        displayMeetingList.clear();
        displayMeetingList.addAll(fullMeetingList);

        meetingAdapter.notifyDataSetChanged();
        setActiveChip(chipAll);  // default aktif "Semua"
    }
}
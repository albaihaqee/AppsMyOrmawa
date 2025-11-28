package com.inovarka.myormawa.views.dashboard.member;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.utils.Constants;

import static android.content.Context.MODE_PRIVATE;

import java.util.ArrayList;
import java.util.List;

public class HomeMemberFragment extends Fragment {

    private TextView txtUserName;
    private LinearLayout containerAnnouncements;
    private List<AnnouncementMeeting> announcementList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupStatusBar();
        initViews(view);
        loadUserData();
        setupAnnouncements();
        setupClickListeners(view);
    }

    private void setupStatusBar() {
        if (getActivity() != null && getActivity().getWindow() != null) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.md_theme_light_surfaceVariant));

            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
            controller.setAppearanceLightStatusBars(false);
        }
    }

    private void initViews(View view) {
        txtUserName = view.findViewById(R.id.txt_user_name);
        containerAnnouncements = view.findViewById(R.id.container_announcements);
    }

    private void loadUserData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        String fullName = prefs.getString(Constants.KEY_FULL_NAME, "User");

        if (txtUserName != null) {
            txtUserName.setText(fullName);
        }
    }

    private void setupAnnouncements() {
        // TODO: Load announcements from API
        // Untuk sementara kosongkan atau buat dummy data
        containerAnnouncements.removeAllViews();

        // Tambahkan empty state jika belum ada pengumuman
        addEmptyStateAnnouncement();
    }

    private void addEmptyStateAnnouncement() {
        TextView emptyText = new TextView(getContext());
        emptyText.setText("Belum ada pengumuman internal");
        emptyText.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_light_secondary));
        emptyText.setTextSize(14);
        emptyText.setPadding(0, 32, 0, 32);
        emptyText.setGravity(android.view.Gravity.CENTER);
        emptyText.setTypeface(getResources().getFont(R.font.poppins_regular));

        containerAnnouncements.addView(emptyText);
    }

    private void setupClickListeners(View view) {
        // Notification Button
        view.findViewById(R.id.btn_notification).setOnClickListener(v -> {
            // TODO: Open notification activity
            // Intent intent = new Intent(getActivity(), NotificationActivity.class);
            // startActivity(intent);
        });

        // Anggota Button
        view.findViewById(R.id.btn_anggota).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MemberActivity.class);
            startActivity(intent);
        });

        // Absensi Button
        view.findViewById(R.id.btn_absensi).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PresenceHistoryActivity.class);
            startActivity(intent);
        });

        // Kegiatan Button
        view.findViewById(R.id.btn_kegiatan).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MeetingActivity.class);
            startActivity(intent);
        });

        // Dokumen Button
        view.findViewById(R.id.btn_dokumen).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DocumentActivity.class);
            startActivity(intent);
        });

        // See All Announcements Button
        view.findViewById(R.id.txt_see_all_announcements).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MeetingActivity.class);
            startActivity(intent);
        });
        loadAnnouncements();
    }

    private void loadAnnouncements() {
        announcementList = new ArrayList<>();

        // Data dummy - ambil dari API atau database
        // Ini adalah kegiatan yang akan datang (upcoming meetings)
        announcementList.add(new AnnouncementMeeting(
                "1",
                "PENTING: Rapat Bulanan",
                "Rapat Koordinasi Tim",
                "Hari Ini, 20.00",
                "Gedung JTI Lantai 1",
                "Agenda: Evaluasi kegiatan & planning event",
                true // isImportant
        ));

        announcementList.add(new AnnouncementMeeting(
                "2",
                "INFO: Workshop",
                "Workshop Android Development",
                "Besok, 13.00",
                "Lab Komputer A",
                "Agenda: Pelatihan pembuatan aplikasi mobile",
                false
        ));

        announcementList.add(new AnnouncementMeeting(
                "3",
                "REMINDER: Seminar",
                "Seminar Teknologi AI",
                "27 Nov, 10.00",
                "Auditorium Utama",
                "Agenda: Pengenalan AI dalam software development",
                false
        ));

        // Display announcements
        displayAnnouncements();
    }

    private void displayAnnouncements() {
        if (containerAnnouncements == null) return;

        containerAnnouncements.removeAllViews();

        // Maksimal tampilkan 3 pengumuman teratas
        int maxDisplay = Math.min(announcementList.size(), 3);

        for (int i = 0; i < maxDisplay; i++) {
            AnnouncementMeeting announcement = announcementList.get(i);
            View itemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_announcement_meeting, containerAnnouncements, false);

            TextView tvStatus = itemView.findViewById(R.id.tv_announcement_status);
            TextView tvTitle = itemView.findViewById(R.id.tv_announcement_title);
            TextView tvDate = itemView.findViewById(R.id.tv_announcement_date);
            TextView tvLocation = itemView.findViewById(R.id.tv_announcement_location);
            TextView tvAgenda = itemView.findViewById(R.id.tv_announcement_agenda);

            tvStatus.setText(announcement.getStatus());
            tvTitle.setText(announcement.getTitle());
            tvDate.setText(announcement.getDateTime());
            tvLocation.setText(announcement.getLocation());
            tvAgenda.setText("Agenda: " + announcement.getAgenda());

            // Set color based on importance
            if (announcement.isImportant()) {
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                tvStatus.setTextColor(getResources().getColor(R.color.blue_500));
            }

            // Click listener to open meeting detail or MeetingActivity
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), MeetingActivity.class);
                startActivity(intent);
            });

            containerAnnouncements.addView(itemView);
        }
    }

    // Inner class untuk model announcement
    private static class AnnouncementMeeting {
        private String id;
        private String status;
        private String title;
        private String dateTime;
        private String location;
        private String agenda;
        private boolean isImportant;

        public AnnouncementMeeting(String id, String status, String title, String dateTime,
                                   String location, String agenda, boolean isImportant) {
            this.id = id;
            this.status = status;
            this.title = title;
            this.dateTime = dateTime;
            this.location = location;
            this.agenda = agenda;
            this.isImportant = isImportant;
        }

        public String getId() { return id; }
        public String getStatus() { return status; }
        public String getTitle() { return title; }
        public String getDateTime() { return dateTime; }
        public String getLocation() { return location; }
        public String getAgenda() { return agenda; }
        public boolean isImportant() { return isImportant; }
    }
}
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
import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.models.Meeting;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.network.ApiService;
import com.inovarka.myormawa.utils.Constants;
import com.inovarka.myormawa.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        setupClickListeners(view);
        loadAnnouncements(); // langsung load dari API
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
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, getActivity().MODE_PRIVATE);
        String fullName = prefs.getString(Constants.KEY_FULL_NAME, "User");

        if (txtUserName != null) {
            txtUserName.setText(fullName);
        }
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.btn_anggota).setOnClickListener(v -> startActivity(new Intent(getActivity(), MemberActivity.class)));
        view.findViewById(R.id.btn_absensi).setOnClickListener(v -> startActivity(new Intent(getActivity(), PresenceHistoryActivity.class)));
        view.findViewById(R.id.btn_kegiatan).setOnClickListener(v -> startActivity(new Intent(getActivity(), MeetingActivity.class)));
        view.findViewById(R.id.btn_dokumen).setOnClickListener(v -> startActivity(new Intent(getActivity(), DocumentActivity.class)));
        view.findViewById(R.id.btn_notification).setOnClickListener(v -> startActivity(new Intent(getActivity(), NotificationMemberActivity.class)));
        view.findViewById(R.id.txt_see_all_announcements).setOnClickListener(v -> startActivity(new Intent(getActivity(), MeetingActivity.class)));
    }

    private void loadAnnouncements() {
        containerAnnouncements.removeAllViews(); // clear dulu

        SessionManager sessionManager = new SessionManager(requireContext());
        String ormawaId = sessionManager.getIdOrmawa();

        ApiService api = ApiClient.getApiService();
        api.getKegiatanByOrmawa(ormawaId).enqueue(new Callback<ApiResponseList<Meeting>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Meeting>> call, Response<ApiResponseList<Meeting>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Meeting> meetings = response.body().getData();

                    if (meetings.isEmpty()) {
                        addEmptyStateAnnouncement();
                        return;
                    }

                    announcementList = new ArrayList<>();
                    int maxDisplay = Math.min(meetings.size(), 3);
                    for (int i = 0; i < maxDisplay; i++) {
                        Meeting m = meetings.get(i);
                        announcementList.add(new AnnouncementMeeting(
                                m.getId(),
                                "INFO",
                                m.getNama(),
                                m.getTanggal() + ", " + m.getWaktu(),
                                m.getLokasi(),
                                m.getAgenda(),
                                false
                        ));
                    }
                    displayAnnouncements();
                } else {
                    addEmptyStateAnnouncement();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<Meeting>> call, Throwable t) {
                addEmptyStateAnnouncement();
            }
        });
    }

    private void addEmptyStateAnnouncement() {
        containerAnnouncements.removeAllViews();

        TextView emptyText = new TextView(getContext());
        emptyText.setText("Belum ada pengumuman internal");
        emptyText.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_light_secondary));
        emptyText.setTextSize(14);
        emptyText.setPadding(0, 32, 0, 32);
        emptyText.setGravity(android.view.Gravity.CENTER);
        emptyText.setTypeface(getResources().getFont(R.font.poppins_regular));

        containerAnnouncements.addView(emptyText);
    }

    private void displayAnnouncements() {
        containerAnnouncements.removeAllViews();

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
            tvStatus.setTextColor(announcement.isImportant() ?
                    getResources().getColor(android.R.color.holo_red_dark) :
                    getResources().getColor(R.color.blue_500));

            itemView.setOnClickListener(v -> startActivity(new Intent(getActivity(), MeetingActivity.class)));

            containerAnnouncements.addView(itemView);
        }
    }

    // Inner class untuk model announcement
    private static class AnnouncementMeeting {
        private final String id;
        private final String status;
        private final String title;
        private final String dateTime;
        private final String location;
        private final String agenda;
        private final boolean isImportant;

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

package com.inovarka.myormawa.views.dashboard.member;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeMemberFragment extends Fragment {

    private TextView txtUserName;
    private LinearLayout containerAnnouncements;
    private FrameLayout btnNotification;
    private View badgeNotif;

    private SharedPreferences prefs;
    private static final String KEY_LAST_SEEN = "last_seen_count";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupStatusBar();
        prefs = requireContext().getSharedPreferences("notif_pref", getActivity().MODE_PRIVATE);

        initViews(view);
        loadUserData();
        setupClickListeners(view);
        loadAnnouncements();
        loadNotificationBadge();
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
        btnNotification = view.findViewById(R.id.btn_notification);
        badgeNotif = btnNotification.findViewById(R.id.badge_notification);
    }

    private void loadUserData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, getActivity().MODE_PRIVATE);
        String fullName = prefs.getString(Constants.KEY_FULL_NAME, "User");
        txtUserName.setText(fullName);
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.btn_anggota).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MemberActivity.class)));

        view.findViewById(R.id.btn_absensi).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), PresenceHistoryActivity.class)));

        view.findViewById(R.id.btn_kegiatan).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MeetingActivity.class)));

        view.findViewById(R.id.btn_dokumen).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DocumentActivity.class)));

        // ========= FIXED: update lastSeen saat buka notifikasi ==========
        btnNotification.setOnClickListener(v -> {
            badgeNotif.setVisibility(View.GONE);
            updateLastSeenCount();
            startActivity(new Intent(getActivity(), NotificationMemberActivity.class));
        });

        view.findViewById(R.id.txt_see_all_announcements)
                .setOnClickListener(v -> startActivity(new Intent(getActivity(), MeetingActivity.class)));
    }

    private void loadAnnouncements() {
        containerAnnouncements.removeAllViews();

        SessionManager sessionManager = new SessionManager(requireContext());
        String ormawaId = sessionManager.getIdOrmawa();

        ApiService api = ApiClient.getApiService();
        api.getKegiatanByOrmawa(ormawaId).enqueue(new Callback<ApiResponseList<Meeting>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Meeting>> call, Response<ApiResponseList<Meeting>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    showEmptyState();
                    return;
                }

                List<Meeting> list = response.body().getData();
                if (list == null || list.isEmpty()) {
                    showEmptyState();
                    return;
                }

                int max = Math.min(3, list.size());
                for (int i = 0; i < max; i++) {
                    addAnnouncementItem(list.get(i));
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<Meeting>> call, Throwable t) {
                showEmptyState();
            }
        });
    }

    private void showEmptyState() {
        containerAnnouncements.removeAllViews();

        TextView tv = new TextView(getContext());
        tv.setText("Belum ada pengumuman internal");
        tv.setTextSize(14);
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_light_secondary));
        tv.setPadding(0, 32, 0, 32);
        tv.setGravity(android.view.Gravity.CENTER);

        containerAnnouncements.addView(tv);
    }

    private void addAnnouncementItem(Meeting m) {
        View item = LayoutInflater.from(getContext())
                .inflate(R.layout.item_announcement_meeting, containerAnnouncements, false);

        ((TextView) item.findViewById(R.id.tv_announcement_status)).setText("INFO");
        ((TextView) item.findViewById(R.id.tv_announcement_title)).setText(m.getNama());
        ((TextView) item.findViewById(R.id.tv_announcement_date))
                .setText(m.getTanggal() + " • " + m.getJamMulai());
        ((TextView) item.findViewById(R.id.tv_announcement_location)).setText(m.getLokasi());
        ((TextView) item.findViewById(R.id.tv_announcement_agenda))
                .setText("Agenda: " + m.getAgenda());

        containerAnnouncements.addView(item);
    }

    // =====================================================
    //            NOTIFICATION BADGE HANDLING
    // =====================================================
    private void loadNotificationBadge() {
        SessionManager sessionManager = new SessionManager(requireContext());
        String ormawaId = sessionManager.getIdOrmawa();

        ApiService api = ApiClient.getApiService();
        api.getKegiatanByOrmawa(ormawaId).enqueue(new Callback<ApiResponseList<Meeting>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Meeting>> call, Response<ApiResponseList<Meeting>> resp) {
                if (!resp.isSuccessful() || resp.body() == null) return;

                List<Meeting> list = resp.body().getData();
                if (list == null) return;

                int lastSeen = prefs.getInt(KEY_LAST_SEEN, 0);
                int current = list.size();

                if (current > lastSeen) {
                    badgeNotif.setVisibility(View.VISIBLE);
                } else {
                    badgeNotif.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<Meeting>> call, Throwable t) {}
        });
    }

    // ============= FIX: update last seen saat buka notif ============
    private void updateLastSeenCount() {
        SessionManager sessionManager = new SessionManager(requireContext());
        String ormawaId = sessionManager.getIdOrmawa();

        ApiService api = ApiClient.getApiService();
        api.getKegiatanByOrmawa(ormawaId).enqueue(new Callback<ApiResponseList<Meeting>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Meeting>> call, Response<ApiResponseList<Meeting>> resp) {
                if (resp.isSuccessful() && resp.body() != null && resp.body().getData() != null) {
                    int current = resp.body().getData().size();
                    prefs.edit().putInt(KEY_LAST_SEEN, current).apply();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<Meeting>> call, Throwable t) {}
        });
    }
}

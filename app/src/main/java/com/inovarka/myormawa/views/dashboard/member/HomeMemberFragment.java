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

public class HomeMemberFragment extends Fragment {

    private TextView txtUserName;
    private LinearLayout containerAnnouncements;

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
            // TODO: Open anggota/member list activity
        });

        // Absensi Button
        view.findViewById(R.id.btn_absensi).setOnClickListener(v -> {
            // TODO: Open absensi activity
        });

        // Kegiatan Button
        view.findViewById(R.id.btn_kegiatan).setOnClickListener(v -> {
            // TODO: Open kegiatan activity
        });

        // Dokumen Button
        view.findViewById(R.id.btn_dokumen).setOnClickListener(v -> {
            // TODO: Open dokumen activity
        });

        // See All Announcements Button
        view.findViewById(R.id.txt_see_all_announcements).setOnClickListener(v -> {
            // TODO: Open all announcements activity
        });
    }
}
package com.inovarka.myormawa.views.dashboard.member;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.inovarka.myormawa.R;

public class DashboardMemberActivity extends AppCompatActivity {

    private LinearLayout navHome, navEvent, navTaskboard, navProfile;
    private ImageView iconHome, iconEvent, iconTaskboard, iconProfile;
    private TextView labelHome, labelEvent, labelTaskboard, labelProfile;
    private FrameLayout navScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGrayStatusBar();
        setContentView(R.layout.activity_dashboard_member);

        initViews();
        setupBottomNavigation();

        if (savedInstanceState == null) {
            loadFragment(new HomeMemberFragment());
            setActiveTab(0);
        }
    }

    private void setGrayStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.md_theme_light_surfaceVariant));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initViews() {
        navHome = findViewById(R.id.nav_home);
        navEvent = findViewById(R.id.nav_event);
        navTaskboard = findViewById(R.id.nav_taskboard);
        navProfile = findViewById(R.id.nav_profile);
        navScan = findViewById(R.id.nav_scan);

        iconHome = findViewById(R.id.icon_home);
        iconEvent = findViewById(R.id.icon_event);
        iconTaskboard = findViewById(R.id.icon_taskboard);
        iconProfile = findViewById(R.id.icon_profile);

        labelHome = findViewById(R.id.label_home);
        labelEvent = findViewById(R.id.label_event);
        labelTaskboard = findViewById(R.id.label_taskboard);
        labelProfile = findViewById(R.id.label_profile);
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            loadFragment(new HomeMemberFragment());
            setActiveTab(0);
        });

        navEvent.setOnClickListener(v -> {
            loadFragment(new EventFragment());
            setActiveTab(1);
        });

        navScan.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardMemberActivity.this, ScanActivity.class);
            startActivity(intent);
        });

        navTaskboard.setOnClickListener(v -> {
            loadFragment(new TaskboardFragment());
            setActiveTab(3);
        });

        navProfile.setOnClickListener(v -> {
            loadFragment(new ProfileMemberFragment());
            setActiveTab(4);
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void setActiveTab(int position) {
        int primaryColor = ContextCompat.getColor(this, R.color.md_theme_light_primary);
        int inactiveColor = ContextCompat.getColor(this, R.color.bottom_nav_icon_default);

        // Reset all tabs
        updateTab(iconHome, labelHome, R.drawable.ic_home, inactiveColor, R.font.poppins_regular);
        updateTab(iconEvent, labelEvent, R.drawable.ic_calendar, inactiveColor, R.font.poppins_regular);
        updateTab(iconTaskboard, labelTaskboard, R.drawable.ic_calendar, inactiveColor, R.font.poppins_regular);
        updateTab(iconProfile, labelProfile, R.drawable.ic_profile, inactiveColor, R.font.poppins_regular);

        // Set active tab
        switch (position) {
            case 0:
                updateTab(iconHome, labelHome, R.drawable.ic_home_filled, primaryColor, R.font.poppins_semibold);
                break;
            case 1:
                updateTab(iconEvent, labelEvent, R.drawable.ic_calendar_filled, primaryColor, R.font.poppins_semibold);
                break;
            case 2:
                // Scan button tetap tidak berubah
                break;
            case 3:
                updateTab(iconTaskboard, labelTaskboard, R.drawable.ic_calendar_filled, primaryColor, R.font.poppins_semibold);
                break;
            case 4:
                updateTab(iconProfile, labelProfile, R.drawable.ic_profile_filled, primaryColor, R.font.poppins_semibold);
                break;
        }
    }

    private void updateTab(ImageView icon, TextView label, int iconRes, int color, int font) {
        icon.setImageResource(iconRes);
        icon.setColorFilter(color);
        label.setTextColor(color);
        label.setTypeface(getResources().getFont(font));
    }
}
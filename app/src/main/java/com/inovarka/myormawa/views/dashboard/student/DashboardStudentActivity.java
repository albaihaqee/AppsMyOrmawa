package com.inovarka.myormawa.views.dashboard.student;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.inovarka.myormawa.R;

public class DashboardStudentActivity extends AppCompatActivity {

    private LinearLayout navHome, navCalendar, navUkm, navProfile;
    private ImageView iconHome, iconCalendar, iconUkm, iconProfile;
    private TextView labelHome, labelCalendar, labelUkm, labelProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGrayStatusBar();
        setContentView(R.layout.activity_dashboard_student);
        initViews();
        setupBottomNavigation();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
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
        navCalendar = findViewById(R.id.nav_calendar);
        navUkm = findViewById(R.id.nav_ukm);
        navProfile = findViewById(R.id.nav_profile);

        iconHome = findViewById(R.id.icon_home);
        iconCalendar = findViewById(R.id.icon_calendar);
        iconUkm = findViewById(R.id.icon_ukm);
        iconProfile = findViewById(R.id.icon_profile);

        labelHome = findViewById(R.id.label_home);
        labelCalendar = findViewById(R.id.label_calendar);
        labelUkm = findViewById(R.id.label_ukm);
        labelProfile = findViewById(R.id.label_profile);
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            setActiveTab(0);
        });

        navCalendar.setOnClickListener(v -> {
            loadFragment(new CalendarFragment());
            setActiveTab(1);
        });

        navUkm.setOnClickListener(v -> {
            loadFragment(new OrganizationFragment());
            setActiveTab(2);
        });

        navProfile.setOnClickListener(v -> {
            loadFragment(new ProfileFragment());
            setActiveTab(3);
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
        updateTab(iconCalendar, labelCalendar, R.drawable.ic_calendar, inactiveColor, R.font.poppins_regular);
        updateTab(iconUkm, labelUkm, R.drawable.ic_organization, inactiveColor, R.font.poppins_regular);
        updateTab(iconProfile, labelProfile, R.drawable.ic_profile, inactiveColor, R.font.poppins_regular);

        // Set active tab
        switch (position) {
            case 0:
                updateTab(iconHome, labelHome, R.drawable.ic_home_filled, primaryColor, R.font.poppins_semibold);
                break;
            case 1:
                updateTab(iconCalendar, labelCalendar, R.drawable.ic_calendar_filled, primaryColor, R.font.poppins_semibold);
                break;
            case 2:
                updateTab(iconUkm, labelUkm, R.drawable.ic_organization_filled, primaryColor, R.font.poppins_semibold);
                break;
            case 3:
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

    public void navigateToOrganization() {
        loadFragment(new OrganizationFragment());
        setActiveTab(2); // Index 2 = Organization/UKM tab
    }
}
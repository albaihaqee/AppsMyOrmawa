package com.inovarka.myormawa.views.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.views.auth.RoleSelectionActivity;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private MaterialButton btnNext, btnPrevious;
    private TextView txtSkip;
    private View indicator1, indicator2, indicator3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWhiteStatusBar();
        setContentView(R.layout.activity_onboarding);
        initViews();
        setupViewPager();
    }

    private void setWhiteStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initViews() {
        viewPager = findViewById(R.id.view_pager);
        btnNext = findViewById(R.id.btn_next);
        btnPrevious = findViewById(R.id.btn_previous);
        txtSkip = findViewById(R.id.txt_skip);
        indicator1 = findViewById(R.id.indicator_1);
        indicator2 = findViewById(R.id.indicator_2);
        indicator3 = findViewById(R.id.indicator_3);

        btnNext.post(() -> {
            btnNext.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_right));
        });

        btnNext.setOnClickListener(v -> handleNext());
        btnPrevious.setOnClickListener(v -> handlePrevious());
        txtSkip.setOnClickListener(v -> finishOnboarding());
    }

    private void setupViewPager() {
        viewPager.setAdapter(new OnboardingAdapter(this));
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateUI(position);
            }
        });
    }

    private void handleNext() {
        int current = viewPager.getCurrentItem();
        if (current < 2) {
            viewPager.setCurrentItem(current + 1);
        } else {
            finishOnboarding();
        }
    }

    private void handlePrevious() {
        int current = viewPager.getCurrentItem();
        if (current > 0) {
            viewPager.setCurrentItem(current - 1);
        }
    }

    private void updateUI(int position) {
        updateIndicators(position);
        updateButtons(position);
    }

    private void updateIndicators(int position) {
        indicator1.setBackgroundResource(R.drawable.shape_indicator_inactive);
        indicator2.setBackgroundResource(R.drawable.shape_indicator_inactive);
        indicator3.setBackgroundResource(R.drawable.shape_indicator_inactive);

        ViewGroup.LayoutParams params1 = indicator1.getLayoutParams();
        params1.width = dpToPx(8);
        params1.height = dpToPx(8);
        indicator1.setLayoutParams(params1);

        ViewGroup.LayoutParams params2 = indicator2.getLayoutParams();
        params2.width = dpToPx(8);
        params2.height = dpToPx(8);
        indicator2.setLayoutParams(params2);

        ViewGroup.LayoutParams params3 = indicator3.getLayoutParams();
        params3.width = dpToPx(8);
        params3.height = dpToPx(8);
        indicator3.setLayoutParams(params3);

        View activeIndicator = null;
        switch (position) {
            case 0:
                activeIndicator = indicator1;
                break;
            case 1:
                activeIndicator = indicator2;
                break;
            case 2:
                activeIndicator = indicator3;
                break;
        }

        if (activeIndicator != null) {
            activeIndicator.setBackgroundResource(R.drawable.shape_indicator_active);
            ViewGroup.LayoutParams activeParams = activeIndicator.getLayoutParams();
            activeParams.width = dpToPx(32);
            activeParams.height = dpToPx(8);
            activeIndicator.setLayoutParams(activeParams);
        }
    }

    private void updateButtons(int position) {
        // Update visibility button Previous
        if (position == 0) {
            btnPrevious.setVisibility(View.GONE);
        } else {
            btnPrevious.setVisibility(View.VISIBLE);
            btnPrevious.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_left));
        }

        if (position == 2) {
            btnNext.setText("Start");
            btnNext.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_rocket));
        } else {
            btnNext.setText("Next");
            btnNext.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_right));
        }
    }

    private void finishOnboarding() {
        startActivity(new Intent(this, RoleSelectionActivity.class));
        finish();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
package com.inovarka.myormawa.views.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.inovarka.myormawa.R;

public class RegisterSuccessActivity extends AppCompatActivity {

    private View viewBlueOverlay, checkIconContainer;
    private ConstraintLayout layoutSuccessContent;
    private MaterialButton btnContinue;
    private String registeredEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.md_theme_light_primary, false);
        setContentView(R.layout.activity_register_success);

        // Get email dari RegisterVerificationActivity
        registeredEmail = getIntent().getStringExtra("email");

        initViews();
        setupBackPressHandler();

        new Handler(Looper.getMainLooper()).postDelayed(this::startSuccessAnimation, 300);
    }

    private void setStatusBarColor(int colorRes, boolean lightMode) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, colorRes));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = lightMode ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : 0;
            window.getDecorView().setSystemUiVisibility(flags);
        }
    }

    private void initViews() {
        viewBlueOverlay = findViewById(R.id.view_blue_overlay);
        layoutSuccessContent = findViewById(R.id.layout_success_content);
        checkIconContainer = findViewById(R.id.check_icon_container);
        btnContinue = findViewById(R.id.btn_continue);

        checkIconContainer.setBackgroundColor(Color.parseColor("#2C4EEF"));
        btnContinue.setOnClickListener(v -> navigateToLogin());
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Prevent back navigation
            }
        });
    }

    private void startSuccessAnimation() {
        layoutSuccessContent.setVisibility(View.VISIBLE);

        viewBlueOverlay.post(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                animateCircularReveal();
            } else {
                animateFadeOut();
            }
        });
    }

    private void animateCircularReveal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] loc = new int[2];
            checkIconContainer.getLocationInWindow(loc);

            int cx = loc[0] + checkIconContainer.getWidth() / 2;
            int cy = loc[1] + checkIconContainer.getHeight() / 2;
            int startRadius = (int) Math.hypot(viewBlueOverlay.getWidth(), viewBlueOverlay.getHeight());
            int endRadius = checkIconContainer.getWidth() / 2;

            Animator reveal = ViewAnimationUtils.createCircularReveal(
                    viewBlueOverlay, cx, cy, startRadius, endRadius);
            reveal.setDuration(1000);
            reveal.setInterpolator(new DecelerateInterpolator(1.5f));

            reveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setStatusBarColor(android.R.color.white, true);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    viewBlueOverlay.setVisibility(View.GONE);
                    checkIconContainer.setBackgroundResource(R.drawable.shape_indicator_success);
                    animateContent();
                }
            });

            reveal.start();
        }
    }

    private void animateFadeOut() {
        ObjectAnimator fade = ObjectAnimator.ofFloat(viewBlueOverlay, "alpha", 1f, 0f);
        fade.setDuration(1000);
        fade.setInterpolator(new DecelerateInterpolator(1.5f));

        fade.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                setStatusBarColor(android.R.color.white, true);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                viewBlueOverlay.setVisibility(View.GONE);
                checkIconContainer.setBackgroundResource(R.drawable.shape_indicator_success);
                animateContent();
            }
        });

        fade.start();
    }

    private void animateContent() {
        View[] views = {
                checkIconContainer,
                findViewById(R.id.txt_success_title),
                findViewById(R.id.txt_success_subtitle),
                btnContinue
        };

        for (View v : views) {
            v.setAlpha(0f);
            v.setTranslationY(30f);
        }

        checkIconContainer.setScaleX(0.3f);
        checkIconContainer.setScaleY(0.3f);

        animateView(checkIconContainer, 150, true);
        animateView(views[1], 350, false);
        animateView(views[2], 500, false);
        animateView(views[3], 650, false);

        ObjectAnimator.ofFloat(layoutSuccessContent, "alpha", 0f, 1f)
                .setDuration(400).start();
    }

    private void animateView(View view, int delay, boolean withScale) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        ObjectAnimator transY = ObjectAnimator.ofFloat(view, "translationY", 30f, 0f);

        alpha.setDuration(500);
        transY.setDuration(withScale ? 600 : 500);
        transY.setInterpolator(new DecelerateInterpolator(1.2f));

        if (withScale) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.3f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.3f, 1f);
            scaleX.setDuration(600);
            scaleY.setDuration(600);
            scaleX.setInterpolator(new OvershootInterpolator(2f));
            scaleY.setInterpolator(new OvershootInterpolator(2f));
            set.playTogether(alpha, transY, scaleX, scaleY);
        } else {
            set.playTogether(alpha, transY);
        }

        set.setStartDelay(delay);
        set.start();
    }

    private void navigateToLogin() {
        btnContinue.animate()
                .scaleX(0.95f).scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> btnContinue.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(100)
                        .withEndAction(() -> {
                            Intent intent = new Intent(this, LoginActivity.class);
                            // Kirim email via Intent Extra (temporary)
                            intent.putExtra("registered_email", registeredEmail);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        })
                        .start())
                .start();
    }
}
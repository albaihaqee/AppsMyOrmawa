package com.inovarka.myormawa.views.splash;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.views.onboarding.OnboardingActivity;

public class SplashActivity extends AppCompatActivity {

    private View viewHole;
    private ImageView imgLogo;
    private TextView txtAppName;

    private static final int DURATION_EMERGE = 1200;
    private static final int DURATION_DESCEND = 900;
    private static final int DURATION_SLIDE = 700;
    private static final int DURATION_WORDMARK = 1000;
    private static final int DELAY_BEFORE_NAVIGATE = 500;

    private PathInterpolator fastOutSlowIn;
    private PathInterpolator emphasizedDecelerate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWhiteStatusBar();
        setContentView(R.layout.activity_splash);
        initInterpolators();
        initViews();
        imgLogo.post(this::startAnimation);
    }

    private void setWhiteStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initInterpolators() {
        fastOutSlowIn = new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
        emphasizedDecelerate = new PathInterpolator(0.05f, 0.7f, 0.1f, 1.0f);
    }

    private void initViews() {
        viewHole = findViewById(R.id.view_hole);
        imgLogo = findViewById(R.id.img_logo);
        txtAppName = findViewById(R.id.txt_appname);
        txtAppName.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    private void startAnimation() {
        float density = getResources().getDisplayMetrics().density;
        String appName = getString(R.string.app_name);

        txtAppName.setText(appName);
        txtAppName.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        float textWidth = txtAppName.getMeasuredWidth();
        txtAppName.setText("");

        float logoWidth = 50f * density;
        float spacing = 12f * density;
        float totalWidth = logoWidth + spacing + textWidth;
        float logoX = -(totalWidth / 2) + (logoWidth / 2);
        float textX = logoX + (logoWidth / 2) + spacing - (textWidth / 4);

        AnimatorSet masterSet = new AnimatorSet();
        masterSet.playSequentially(
                createEmergeAnimation(density),
                createDescendAnimation(density),
                createSlideAnimation(logoX),
                createWordmarkAnimation(appName, textX)
        );

        masterSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                navigateToOnboarding();
            }
        });

        masterSet.start();
    }

    private AnimatorSet createEmergeAnimation(float density) {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator[] logoAnims = {
                ObjectAnimator.ofFloat(imgLogo, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(imgLogo, "scaleX", 0.15f, 1.21f),
                ObjectAnimator.ofFloat(imgLogo, "scaleY", 0.15f, 1.21f),
                ObjectAnimator.ofFloat(imgLogo, "translationY", 0f, -60 * density)
        };

        for (ObjectAnimator anim : logoAnims) {
            anim.setDuration(DURATION_EMERGE);
            anim.setInterpolator(emphasizedDecelerate);
        }

        ObjectAnimator holeFade = ObjectAnimator.ofFloat(viewHole, "alpha", 1f, 0f);
        holeFade.setDuration(DURATION_EMERGE - 300);
        holeFade.setStartDelay(500);
        holeFade.setInterpolator(fastOutSlowIn);

        set.playTogether(logoAnims[0], logoAnims[1], logoAnims[2], logoAnims[3], holeFade);
        return set;
    }

    private AnimatorSet createDescendAnimation(float density) {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator[] anims = {
                ObjectAnimator.ofFloat(imgLogo, "scaleX", 1.21f, 0.71f),
                ObjectAnimator.ofFloat(imgLogo, "scaleY", 1.21f, 0.71f),
                ObjectAnimator.ofFloat(imgLogo, "translationY", -60 * density, 0f)
        };

        for (ObjectAnimator anim : anims) {
            anim.setDuration(DURATION_DESCEND);
            anim.setInterpolator(fastOutSlowIn);
        }

        set.playTogether(anims);
        return set;
    }

    private AnimatorSet createSlideAnimation(float logoX) {
        ObjectAnimator slide = ObjectAnimator.ofFloat(imgLogo, "translationX", 0f, logoX);
        slide.setDuration(DURATION_SLIDE);
        slide.setInterpolator(fastOutSlowIn);

        AnimatorSet set = new AnimatorSet();
        set.play(slide);
        return set;
    }

    private AnimatorSet createWordmarkAnimation(String text, float textX) {
        txtAppName.setAlpha(0f);
        txtAppName.setScaleX(0.9f);
        txtAppName.setScaleY(0.9f);
        txtAppName.setTranslationX(textX - 15);
        txtAppName.setRotationY(8f);
        txtAppName.setText(text);

        ValueAnimator clipReveal = ValueAnimator.ofFloat(0f, 1f);
        clipReveal.setDuration(DURATION_WORDMARK + 600);
        clipReveal.setInterpolator(new PathInterpolator(0.2f, 0f, 0.1f, 1f));
        clipReveal.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            int width = txtAppName.getWidth();
            if (width > 0) {
                txtAppName.setClipBounds(new Rect(0, 0, (int) (width * progress), txtAppName.getHeight()));
            }
        });

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(txtAppName, "alpha", 0f, 1f);
        fadeIn.setDuration(DURATION_WORDMARK + 400);
        fadeIn.setInterpolator(new PathInterpolator(0.25f, 0f, 0.1f, 1f));

        ObjectAnimator[] anims = {
                ObjectAnimator.ofFloat(txtAppName, "scaleX", 0.9f, 1.0f),
                ObjectAnimator.ofFloat(txtAppName, "scaleY", 0.9f, 1.0f),
                ObjectAnimator.ofFloat(txtAppName, "translationX", textX - 15, textX),
                ObjectAnimator.ofFloat(txtAppName, "rotationY", 8f, 0f)
        };

        for (ObjectAnimator anim : anims) {
            anim.setDuration(DURATION_WORDMARK + 600);
            anim.setInterpolator(emphasizedDecelerate);
        }

        AnimatorSet set = new AnimatorSet();
        set.playTogether(clipReveal, fadeIn, anims[0], anims[1], anims[2], anims[3]);
        return set;
    }

    private void navigateToOnboarding() {
        imgLogo.postDelayed(() -> {
            startActivity(new Intent(this, OnboardingActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, DELAY_BEFORE_NAVIGATE);
    }
}
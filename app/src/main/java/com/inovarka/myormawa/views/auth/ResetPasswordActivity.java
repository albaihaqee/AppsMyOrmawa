package com.inovarka.myormawa.views.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.inovarka.myormawa.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWhiteStatusBar();
        setContentView(R.layout.activity_reset_password);

        email = getIntent().getStringExtra("email");

        initViews();
    }

    private void setWhiteStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initViews() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Button "OK" atau "Confirm" untuk lanjut ke SetNewPasswordActivity
        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleConfirm();
            }
        });
    }

    private void handleConfirm() {
        // Navigasi ke SetNewPasswordActivity dengan membawa email
        Intent intent = new Intent(this, SetNewPasswordActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish(); //
    }
}
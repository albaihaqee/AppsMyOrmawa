package com.inovarka.myormawa.views.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.inovarka.myormawa.R;

public class RoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWhiteStatusBar();
        setContentView(R.layout.activity_role_selection);
        initViews();
    }

    private void setWhiteStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initViews() {
        MaterialButton btnStudent = findViewById(R.id.btn_login_student);
        MaterialButton btnAdmin = findViewById(R.id.btn_login_admin);

        btnStudent.setOnClickListener(v -> navigateToLogin("student"));
        btnAdmin.setOnClickListener(v -> navigateToLogin("admin"));
    }

    private void navigateToLogin(String role) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("role", role);
        startActivity(intent);
    }
}
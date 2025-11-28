package com.inovarka.myormawa.views.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.LoginData;
import com.inovarka.myormawa.models.LoginRequest;
import com.inovarka.myormawa.models.LoginResponse;
import com.inovarka.myormawa.models.User;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.utils.Constants;
import com.inovarka.myormawa.views.dashboard.student.DashboardStudentActivity;
import com.inovarka.myormawa.views.dashboard.member.DashboardMemberActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText edtEmail, edtPassword;
    private LinearLayout linearLayoutAuthPrompt;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWhiteStatusBar();
        setContentView(R.layout.activity_login);

        userRole = getIntent().getStringExtra("role");
        if (userRole == null) {
            userRole = "student";
        }

        initViews();
        setupUIBasedOnRole();
        loadEmailFromIntent();
    }

    private void setWhiteStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initViews() {
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        linearLayoutAuthPrompt = findViewById(R.id.linearLayoutAuthPrompt);

        edtEmail.addTextChangedListener(createErrorClearer(tilEmail));
        edtPassword.addTextChangedListener(createErrorClearer(tilPassword));

        findViewById(R.id.btn_login).setOnClickListener(v -> handleLogin());
        findViewById(R.id.txt_register).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
        findViewById(R.id.txt_forgotpw).setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    private void setupUIBasedOnRole() {
        if ("member".equals(userRole)) {
            linearLayoutAuthPrompt.setVisibility(View.GONE);
        } else {
            linearLayoutAuthPrompt.setVisibility(View.VISIBLE);
        }
    }

    private void loadEmailFromIntent() {
        String email = getIntent().getStringExtra("registered_email");
        if (email != null && !email.isEmpty()) {
            edtEmail.setText(email);
            edtPassword.requestFocus();
        }
    }

    private TextWatcher createErrorClearer(TextInputLayout layout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    private void handleLogin() {
        if (!validateInputs()) return;

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        findViewById(R.id.btn_login).setEnabled(false);

        LoginRequest request = new LoginRequest("login", email, password);

        ApiClient.getApiService().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                findViewById(R.id.btn_login).setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess()) {
                        User user = loginResponse.getData().getUser();
                        int userLevel = user.getLevel();

                        if ("student".equals(userRole)) {
                            if (userLevel == 3) {
                                saveUserData(loginResponse.getData());
                                Toast.makeText(LoginActivity.this,
                                        "Berhasil login sebagai Mahasiswa!", Toast.LENGTH_SHORT).show();
                                navigateToDashboard(DashboardStudentActivity.class);
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Gagal, Anda tidak memiliki akses sebagai mahasiswa.",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else if ("member".equals(userRole)) {
                            if (userLevel == 4) {
                                saveUserData(loginResponse.getData());
                                Toast.makeText(LoginActivity.this,
                                        "Berhasil login sebagai Pengurus!", Toast.LENGTH_SHORT).show();
                                navigateToDashboard(DashboardMemberActivity.class);
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Maaf, Anda belum terdaftar sebagai pengurus. Silakan login sebagai mahasiswa.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Login gagal. Periksa kembali email dan password Anda.",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Login gagal. Periksa kembali email dan password Anda.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                findViewById(R.id.btn_login).setEnabled(true);
                Toast.makeText(LoginActivity.this,
                        "Koneksi error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LoginActivity", "Error: " + t.getMessage(), t);
            }
        });
    }

    private void navigateToDashboard(Class<?> dashboardClass) {
        Intent intent = new Intent(this, dashboardClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void saveUserData(LoginData data) {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        User user = data.getUser();

        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.putString(Constants.KEY_TOKEN, data.getToken());
        editor.putInt(Constants.KEY_USER_ID, user.getId());
        editor.putString(Constants.KEY_NIM, user.getNim());
        editor.putString(Constants.KEY_FULL_NAME, user.getFullName());
        editor.putString(Constants.KEY_EMAIL, user.getEmail());
        editor.putString(Constants.KEY_PROGRAM_STUDI, user.getProgramStudi());
        editor.putString(Constants.KEY_ANGKATAN, user.getAngkatan());
        editor.putInt(Constants.KEY_LEVEL, user.getLevel());

        editor.apply();
    }

    private boolean validateInputs() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            edtEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email format");
            edtEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            edtPassword.requestFocus();
            return false;
        }

        if (password.length() < 8) {
            tilPassword.setError("Password must be at least 8 characters");
            edtPassword.requestFocus();
            return false;
        }

        return true;
    }
}
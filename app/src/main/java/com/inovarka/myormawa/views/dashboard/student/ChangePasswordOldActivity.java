package com.inovarka.myormawa.views.dashboard.student;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.LoginRequest;
import com.inovarka.myormawa.models.LoginResponse;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordOldActivity extends AppCompatActivity {

    private static final int MAX_ATTEMPTS = 3;

    private ImageView btnBack;
    private TextInputLayout tilPassword;
    private TextInputEditText edtPassword;
    private MaterialButton btnContinue;

    private int attemptCount = 0;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_change_password_old);

        loadUserEmail();
        initViews();
        setupListeners();
    }

    private void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));
    }

    private void loadUserEmail() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userEmail = prefs.getString(Constants.KEY_EMAIL, "");
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tilPassword = findViewById(R.id.til_password_lama);
        edtPassword = findViewById(R.id.edt_password_lama);
        btnContinue = findViewById(R.id.btn_continue);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError(null);
                String input = s.toString().trim();

                // Button enabled jika minimal 8 karakter
                boolean enabled = input.length() >= 8;
                setButtonState(enabled);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnContinue.setOnClickListener(v -> validatePassword());
    }

    private void validatePassword() {
        String oldPassword = edtPassword.getText().toString().trim();

        if (oldPassword.isEmpty()) {
            tilPassword.setError("Password tidak boleh kosong");
            return;
        }

        if (oldPassword.length() < 8) {
            tilPassword.setError("Password minimal 8 karakter");
            return;
        }

        // Cek apakah sudah mencapai maksimal percobaan
        if (attemptCount >= MAX_ATTEMPTS) {
            Toast.makeText(this, "Terlalu banyak percobaan gagal. Silakan coba lagi nanti.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Disable button saat loading
        btnContinue.setEnabled(false);

        // Verifikasi password lama dengan API login
        LoginRequest request = new LoginRequest("login", userEmail, oldPassword);

        ApiClient.getApiService().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnContinue.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess()) {
                        // Password benar, lanjut ke ChangePasswordNewActivity
                        Intent intent = new Intent(ChangePasswordOldActivity.this, ChangePasswordNewActivity.class);
                        intent.putExtra("old_password", oldPassword);
                        startActivity(intent);
                        finish();
                    } else {
                        // Password salah
                        attemptCount++;
                        int remainingAttempts = MAX_ATTEMPTS - attemptCount;

                        if (remainingAttempts > 0) {
                            tilPassword.setError("Password salah. Sisa percobaan: " + remainingAttempts);
                        } else {
                            Toast.makeText(ChangePasswordOldActivity.this,
                                    "Terlalu banyak percobaan gagal. Silakan coba lagi nanti.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(ChangePasswordOldActivity.this,
                            "Gagal memverifikasi password. Silakan coba lagi.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnContinue.setEnabled(true);
                Toast.makeText(ChangePasswordOldActivity.this,
                        "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("VerifyPassword", "Error: " + t.getMessage(), t);
            }
        });
    }

    private void setButtonState(boolean enabled) {
        btnContinue.setEnabled(enabled);
        btnContinue.setAlpha(enabled ? 1.0f : 0.5f);
    }
}
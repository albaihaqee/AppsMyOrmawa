package com.inovarka.myormawa.views.dashboard.member;

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
import com.inovarka.myormawa.models.ApiResponse;
import com.inovarka.myormawa.models.ChangePasswordRequest;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.utils.Constants;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordNewMemberActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextInputLayout tilPasswordBaru, tilKonfirmasi;
    private TextInputEditText edtPasswordBaru, edtKonfirmasi;
    private MaterialButton btnContinue;

    private String userEmail;
    private String oldPassword;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_change_password_new_member);

        loadUserData();
        initViews();
        setupListeners();
    }

    private void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userEmail = prefs.getString(Constants.KEY_EMAIL, "");
        username = prefs.getString(Constants.KEY_FULL_NAME, "");

        // Get old password dari intent
        oldPassword = getIntent().getStringExtra("old_password");
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tilPasswordBaru = findViewById(R.id.til_password_baru);
        tilKonfirmasi = findViewById(R.id.til_konfirmasi_password);
        edtPasswordBaru = findViewById(R.id.edt_password_baru);
        edtKonfirmasi = findViewById(R.id.edt_konfirmasi_password);
        btnContinue = findViewById(R.id.btn_continue);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswords();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        edtPasswordBaru.addTextChangedListener(watcher);
        edtKonfirmasi.addTextChangedListener(watcher);

        btnContinue.setOnClickListener(v -> savePassword());
    }

    private void validatePasswords() {
        String newPass = edtPasswordBaru.getText().toString().trim();
        String confirmPass = edtKonfirmasi.getText().toString().trim();

        tilPasswordBaru.setError(null);
        tilKonfirmasi.setError(null);

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            setButtonState(false);
            return;
        }

        boolean isValid = isPasswordValid(newPass);
        boolean isMatch = newPass.equals(confirmPass);

        // Tampilkan error jika ada
        if (!isValid && !newPass.isEmpty()) {
            tilPasswordBaru.setError("Password tidak memenuhi syarat");
        }
        if (!confirmPass.isEmpty() && !isMatch) {
            tilKonfirmasi.setError("Password tidak cocok");
        }

        // Button enabled hanya jika valid dan match
        setButtonState(isValid && isMatch);
    }

    private boolean isPasswordValid(String pass) {
        // 1. Panjang 8-12 karakter
        if (pass.length() < 8 || pass.length() > 12) {
            return false;
        }

        // 2. Minimal 1 huruf kapital
        if (!Pattern.compile("[A-Z]").matcher(pass).find()) {
            return false;
        }

        // 3. Minimal 1 huruf kecil
        if (!Pattern.compile("[a-z]").matcher(pass).find()) {
            return false;
        }

        // 4. Minimal 1 angka
        if (!Pattern.compile("[0-9]").matcher(pass).find()) {
            return false;
        }

        // 5. Tidak boleh mengandung spasi
        if (pass.contains(" ")) {
            return false;
        }

        // 6. Tidak boleh sama dengan username
        if (!username.isEmpty() && pass.equalsIgnoreCase(username)) {
            return false;
        }

        return true;
    }

    private void savePassword() {
        String newPass = edtPasswordBaru.getText().toString().trim();
        String confirmPass = edtKonfirmasi.getText().toString().trim();

        if (!isPasswordValid(newPass)) {
            Toast.makeText(this, "Password tidak memenuhi syarat", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button saat loading
        btnContinue.setEnabled(false);

        // Create request
        ChangePasswordRequest request = new ChangePasswordRequest("change_password", userEmail, oldPassword, newPass);

        // Call API
        ApiClient.getApiService().changePassword(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                btnContinue.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        Toast.makeText(ChangePasswordNewMemberActivity.this,
                                "Password berhasil diubah!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ChangePasswordNewMemberActivity.this,
                                apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ChangePasswordNewMemberActivity.this,
                            "Failed to change password. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                btnContinue.setEnabled(true);
                Toast.makeText(ChangePasswordNewMemberActivity.this,
                        "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ChangePassword", "Error: " + t.getMessage(), t);
            }
        });
    }

    private void setButtonState(boolean enabled) {
        btnContinue.setEnabled(enabled);
        btnContinue.setAlpha(enabled ? 1.0f : 0.5f);
    }
}
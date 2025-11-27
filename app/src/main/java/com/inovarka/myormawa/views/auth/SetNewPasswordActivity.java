package com.inovarka.myormawa.views.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.ApiResponse;
import com.inovarka.myormawa.models.ResetPasswordRequest;
import com.inovarka.myormawa.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetNewPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilPassword, tilConfirmPassword;
    private TextInputEditText edtPassword, edtConfirmPassword;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWhiteStatusBar();
        setContentView(R.layout.activity_set_new_password);

        email = getIntent().getStringExtra("email");

        initViews();
    }

    private void setWhiteStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initViews() {
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        edtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilConfirmPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_update_password).setOnClickListener(v -> handleUpdatePassword());
    }

    private void handleUpdatePassword() {
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (!validateInputs(password, confirmPassword)) {
            return;
        }

        // Disable button saat loading
        findViewById(R.id.btn_update_password).setEnabled(false);

        // Create request
        ResetPasswordRequest request = new ResetPasswordRequest("reset_password", email, password);

        // Call API
        ApiClient.getApiService().resetPassword(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                findViewById(R.id.btn_update_password).setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        Toast.makeText(SetNewPasswordActivity.this,
                                "Password updated successfully!", Toast.LENGTH_SHORT).show();

                        // Navigate to success screen
                        Intent intent = new Intent(SetNewPasswordActivity.this, ResetPasswordSuccessActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SetNewPasswordActivity.this,
                                apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SetNewPasswordActivity.this,
                            "Failed to reset password. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                findViewById(R.id.btn_update_password).setEnabled(true);
                Toast.makeText(SetNewPasswordActivity.this,
                        "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ResetPassword", "Error: " + t.getMessage(), t);
            }
        });
    }

    private boolean validateInputs(String password, String confirmPassword) {
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

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Please confirm your password");
            edtConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            edtConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }
}
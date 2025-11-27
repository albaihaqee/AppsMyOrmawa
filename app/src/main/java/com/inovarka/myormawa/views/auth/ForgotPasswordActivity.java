package com.inovarka.myormawa.views.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.ApiResponse;
import com.inovarka.myormawa.models.ForgotPasswordRequest;
import com.inovarka.myormawa.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private TextInputEditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWhiteStatusBar();
        setContentView(R.layout.activity_forgot_password);
        initViews();
    }

    private void setWhiteStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initViews() {
        tilEmail = findViewById(R.id.til_email);
        edtEmail = findViewById(R.id.edt_email);

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilEmail.setError(null);
            }
        });

        findViewById(R.id.btn_reset_password).setOnClickListener(v -> handleResetPassword());
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void handleResetPassword() {
        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            edtEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email format");
            edtEmail.requestFocus();
            return;
        }

        // Disable button saat loading
        findViewById(R.id.btn_reset_password).setEnabled(false);

        // Create request
        ForgotPasswordRequest request = new ForgotPasswordRequest("forgot_password", email);

        // Call API
        ApiClient.getApiService().forgotPassword(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                findViewById(R.id.btn_reset_password).setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        // Navigate to verification
                        Intent intent = new Intent(ForgotPasswordActivity.this, ForgotPasswordVerificationActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this,
                                apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Failed to send reset code. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                findViewById(R.id.btn_reset_password).setEnabled(true);
                Toast.makeText(ForgotPasswordActivity.this,
                        "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ForgotPassword", "Error: " + t.getMessage(), t);
            }
        });
    }
}
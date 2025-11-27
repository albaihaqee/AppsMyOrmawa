package com.inovarka.myormawa.views.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.ApiResponse;
import com.inovarka.myormawa.models.ResendOtpRequest;
import com.inovarka.myormawa.models.VerifyOtpRequest;
import com.inovarka.myormawa.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordVerificationActivity extends AppCompatActivity {

    private EditText[] codeInputs;
    private TextView txtEmailInfo;
    private ProgressBar progressLoading;
    private String email;
    private boolean isVerifying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWhiteStatusBar();
        setContentView(R.layout.activity_forgot_password_verification);

        email = getIntent().getStringExtra("email");
        initViews();
        setupCodeInputs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetCodeInputs();
    }

    private void setWhiteStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initViews() {
        txtEmailInfo = findViewById(R.id.txt_email_info);
        progressLoading = findViewById(R.id.progress_loading);

        codeInputs = new EditText[]{
                findViewById(R.id.edt_code_1),
                findViewById(R.id.edt_code_2),
                findViewById(R.id.edt_code_3),
                findViewById(R.id.edt_code_4),
                findViewById(R.id.edt_code_5),
                findViewById(R.id.edt_code_6)
        };

        if (email != null) {
            txtEmailInfo.setText(email);
        }

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_verify_code).setOnClickListener(v -> handleVerifyCode());
        findViewById(R.id.txt_resend).setOnClickListener(v -> handleResend());
    }

    private void setupCodeInputs() {
        for (int i = 0; i < codeInputs.length; i++) {
            final int index = i;
            codeInputs[i].addTextChangedListener(createCodeWatcher(index));
            codeInputs[i].setOnKeyListener(createKeyListener(index));
        }
    }

    private TextWatcher createCodeWatcher(int index) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    if (index < codeInputs.length - 1) {
                        codeInputs[index + 1].requestFocus();
                    } else if (isAllFieldsFilled()) {
                        autoVerifyCode();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    private View.OnKeyListener createKeyListener(int index) {
        return (v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (codeInputs[index].getText().toString().isEmpty() && index > 0) {
                    codeInputs[index - 1].requestFocus();
                    codeInputs[index - 1].setText("");
                }
            }
            return false;
        };
    }

    private void resetCodeInputs() {
        for (EditText input : codeInputs) {
            input.setText("");
            input.setEnabled(true);
        }
        codeInputs[0].requestFocus();
        findViewById(R.id.btn_verify_code).setEnabled(true);
        progressLoading.setVisibility(View.GONE);
        isVerifying = false;
    }

    private boolean isAllFieldsFilled() {
        for (EditText input : codeInputs) {
            if (input.getText().toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void autoVerifyCode() {
        if (isVerifying) return;
        verifyCode();
    }

    private void handleVerifyCode() {
        if (!isAllFieldsFilled()) {
            Toast.makeText(this, "Please enter complete verification code", Toast.LENGTH_SHORT).show();
            return;
        }
        verifyCode();
    }

    private void verifyCode() {
        setCodeInputsEnabled(false);
        progressLoading.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_verify_code).setEnabled(false);
        isVerifying = true;

        String code = getCodeFromInputs();

        // Create request
        VerifyOtpRequest request = new VerifyOtpRequest("verify_otp", email, code, "forgot_password");

        // Call API
        ApiClient.getApiService().verifyOtp(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressLoading.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        verifyCodeSuccess();
                    } else {
                        Toast.makeText(ForgotPasswordVerificationActivity.this,
                                apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        resetCodeInputs();
                    }
                } else {
                    Toast.makeText(ForgotPasswordVerificationActivity.this,
                            "Verification failed. Please try again.", Toast.LENGTH_SHORT).show();
                    resetCodeInputs();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressLoading.setVisibility(View.GONE);
                Toast.makeText(ForgotPasswordVerificationActivity.this,
                        "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("VerifyOTP", "Error: " + t.getMessage(), t);
                resetCodeInputs();
            }
        });
    }

    private String getCodeFromInputs() {
        StringBuilder code = new StringBuilder();
        for (EditText input : codeInputs) {
            code.append(input.getText().toString().trim());
        }
        return code.toString();
    }

    private void verifyCodeSuccess() {
        progressLoading.setVisibility(View.GONE);
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    private void setCodeInputsEnabled(boolean enabled) {
        for (EditText input : codeInputs) {
            input.setEnabled(enabled);
        }
    }

    private void handleResend() {
        // Create request
        ResendOtpRequest request = new ResendOtpRequest("resend_otp", email, "forgot_password");

        // Call API
        ApiClient.getApiService().resendOtp(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Toast.makeText(ForgotPasswordVerificationActivity.this,
                            apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    if (apiResponse.isSuccess()) {
                        resetCodeInputs();
                    }
                } else {
                    Toast.makeText(ForgotPasswordVerificationActivity.this,
                            "Failed to resend code", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ForgotPasswordVerificationActivity.this,
                        "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ResendOTP", "Error: " + t.getMessage(), t);
            }
        });
    }
}
package com.inovarka.myormawa.views.dashboard.member;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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
import com.inovarka.myormawa.models.ChangeEmailRequest;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeEmailMemberActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextInputLayout tilEmail;
    private TextInputEditText edtEmail;
    private MaterialButton btnContinue;
    private String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_change_email_member);

        loadCurrentEmail();
        initViews();
        setupListeners();
    }

    private void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));
    }

    private void loadCurrentEmail() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        currentEmail = prefs.getString(Constants.KEY_EMAIL, "");
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tilEmail = findViewById(R.id.til_email);
        edtEmail = findViewById(R.id.edt_email);
        btnContinue = findViewById(R.id.btn_continue);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnContinue.setOnClickListener(v -> handleContinue());
    }

    private void handleContinue() {
        String newEmail = edtEmail.getText().toString().trim();

        if (!validateEmailFinal(newEmail)) return;

        // Disable button saat loading
        btnContinue.setEnabled(false);

        // Create request
        ChangeEmailRequest request = new ChangeEmailRequest("change_email", currentEmail, newEmail);

        // Call API
        ApiClient.getApiService().changeEmail(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                btnContinue.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        Toast.makeText(ChangeEmailMemberActivity.this,
                                apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        // Navigate to verification
                        Intent intent = new Intent(ChangeEmailMemberActivity.this, ChangeEmailVerificationMemberActivity.class);
                        intent.putExtra("current_email", currentEmail);
                        intent.putExtra("new_email", newEmail);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ChangeEmailMemberActivity.this,
                                apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ChangeEmailMemberActivity.this,
                            "Failed to send verification code. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                btnContinue.setEnabled(true);
                Toast.makeText(ChangeEmailMemberActivity.this,
                        "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ChangeEmail", "Error: " + t.getMessage(), t);
            }
        });
    }

    private void validateEmail(String email) {
        tilEmail.setError(null);

        if (email.isEmpty()) {
            setButtonState(false);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setButtonState(false);
            return;
        }

        // Validasi domain email (sesuaikan dengan requirement)
        boolean isValidDomain = email.endsWith("@gmail.com") ||
                email.endsWith("@student.polije.ac.id");
        setButtonState(isValidDomain);
    }

    private boolean validateEmailFinal(String email) {
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

        if (email.equals(currentEmail)) {
            tilEmail.setError("New email cannot be the same as current email");
            edtEmail.requestFocus();
            return false;
        }

        return true;
    }

    private void setButtonState(boolean enabled) {
        btnContinue.setEnabled(enabled);
        btnContinue.setAlpha(enabled ? 1.0f : 0.5f);
    }
}
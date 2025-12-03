package com.inovarka.myormawa.views.dashboard.member;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.ApiResponseSingle;
import com.inovarka.myormawa.models.AttendanceData;
import com.inovarka.myormawa.models.AttendanceRequest;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceConfirmationActivity extends AppCompatActivity {

    private TextView tvEventName, tvEventDate, tvEventTime, tvLocation, tvOrganization, tvCurrentTime;
    private MaterialButton btnConfirmCheckIn, btnCancel;
    private ProgressBar progressBar;

    private String qrCode, userId;
    private boolean locationRequired = false;
    private Double currentLat = null, currentLng = null;

    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String> requestLocationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) fetchLastLocation();
                else Toast.makeText(this, "Izin lokasi ditolak", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_confirmation);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        tvEventName = findViewById(R.id.tv_event_name);
        tvEventDate = findViewById(R.id.tv_event_date);
        tvEventTime = findViewById(R.id.tv_event_time);
        tvLocation = findViewById(R.id.tv_location);
        tvOrganization = findViewById(R.id.tv_organization);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        btnConfirmCheckIn = findViewById(R.id.btn_confirm_check_in);
        btnCancel = findViewById(R.id.btn_cancel);
        progressBar = findViewById(R.id.progress_bar);

        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
        if (userId == null) {
            Toast.makeText(this, "User tidak teridentifikasi", Toast.LENGTH_SHORT).show();
            finish();
        }

        qrCode = getIntent().getStringExtra("qr_code");
        tvEventName.setText(getIntent().getStringExtra("event_name"));
        tvEventDate.setText(getIntent().getStringExtra("event_date"));
        tvEventTime.setText(getIntent().getStringExtra("event_time"));
        tvLocation.setText(getIntent().getStringExtra("location"));
        tvOrganization.setText(getIntent().getStringExtra("organization_name"));
        locationRequired = "1".equals(getIntent().getStringExtra("location_required"));

        btnCancel.setOnClickListener(v -> finish());
        btnConfirmCheckIn.setOnClickListener(v -> {
            if (locationRequired && (currentLat == null || currentLng == null)) {
                ensureLocationPermissionAndFetch();
                Toast.makeText(this, "Menunggu lokasi...", Toast.LENGTH_SHORT).show();
                return;
            }
            checkInAttendance();
        });

        updateCurrentTime();
        if (locationRequired) ensureLocationPermissionAndFetch();
    }

    private void ensureLocationPermissionAndFetch() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchLastLocation();
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();
                }
            });
        }
    }

    private void checkInAttendance() {
        showLoading(true);
        AttendanceRequest request = new AttendanceRequest("check_in", userId, qrCode, currentLat, currentLng);

        ApiClient.getApiService().checkInAttendance(request).enqueue(new Callback<ApiResponseSingle<AttendanceData>>() {
            @Override
            public void onResponse(Call<ApiResponseSingle<AttendanceData>> call, Response<ApiResponseSingle<AttendanceData>> response) {
                showLoading(false);
                String msg = "Gagal check-in";
                if (response.isSuccessful() && response.body() != null) {
                    msg = response.body().getMessage() != null ? response.body().getMessage() : msg;
                    if (response.body().isSuccess()) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("check_in_success", true);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                        return;
                    }
                }
                Toast.makeText(AttendanceConfirmationActivity.this, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ApiResponseSingle<AttendanceData>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(AttendanceConfirmationActivity.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
        btnConfirmCheckIn.setEnabled(!show);
    }

    private void updateCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        tvCurrentTime.setText(sdf.format(new Date()));
        tvCurrentTime.postDelayed(this::updateCurrentTime, 1000);
    }
}

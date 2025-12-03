package com.inovarka.myormawa.views.dashboard.member;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.android.material.button.MaterialButton;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.ApiResponseSingle;
import com.inovarka.myormawa.models.AttendanceData;
import com.inovarka.myormawa.models.AttendanceRequest;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.utils.SessionManager;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends AppCompatActivity {

    private CodeScanner codeScanner;
    private CodeScannerView scannerView;
    private ImageView btnBack, btnFlash;
    private MaterialButton btnGallery;
    private boolean isFlashOn = false;
    private boolean isProcessing = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private SessionManager sessionManager;

    // Permission launchers
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) setupScanner();
                else {
                    Toast.makeText(this, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    private final ActivityResultLauncher<String> requestStoragePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) launchGallery();
                else Toast.makeText(this, "Izin galeri diperlukan", Toast.LENGTH_SHORT).show();
            });

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) decodeQRFromImage(uri);
                }
            });

    private final ActivityResultLauncher<Intent> attendanceLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                isProcessing = false;
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    boolean success = result.getData().getBooleanExtra("check_in_success", false);
                    if (success) {
                        Intent backIntent = new Intent();
                        backIntent.putExtra("attendance_success", true);
                        setResult(RESULT_OK, backIntent);
                        finish();
                        return;
                    }
                }
                restartScanner();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        sessionManager = new SessionManager(this);

        scannerView = findViewById(R.id.scanner_view);
        btnBack = findViewById(R.id.btn_back);
        btnFlash = findViewById(R.id.btn_flash);
        btnGallery = findViewById(R.id.btn_upload_qr); // MaterialButton untuk upload QR dari galeri

        btnBack.setOnClickListener(v -> finish());
        btnFlash.setOnClickListener(v -> toggleFlash());
        btnGallery.setOnClickListener(v -> {
            if (hasStoragePermission()) {
                launchGallery();
            } else {
                requestStoragePermission();
            }
        });

        scannerView.setOnClickListener(v -> {
            if (codeScanner != null && !isProcessing) restartScanner();
        });

        checkCameraPermission();
    }

    /** Camera permission **/
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            setupScanner();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /** Storage permission fix Android 13+ **/
    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestStoragePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    /** Camera scanner **/
    private void setupScanner() {
        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setDecodeCallback(result -> mainHandler.post(() -> {
            if (!isProcessing) handleScanResult(result.getText());
        }));
        codeScanner.startPreview();
    }

    private void restartScanner() {
        if (codeScanner != null && !isProcessing) codeScanner.startPreview();
    }

    private void stopScanner() {
        if (codeScanner != null) codeScanner.releaseResources();
    }

    private void toggleFlash() {
        if (codeScanner == null) return;
        isFlashOn = !isFlashOn;
        codeScanner.setFlashEnabled(isFlashOn);
        btnFlash.setImageResource(isFlashOn ? R.drawable.ic_flash_on : R.drawable.ic_flash_off);
    }

    /** Launch gallery **/
    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    /** Decode QR from image **/
    private void decodeQRFromImage(Uri uri) {
        try {
            InputStream stream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            if (bitmap == null) return;

            // Hapus alpha channel
            bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);

            // Resize agar tidak terlalu besar
            int maxDimension = 1000;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scale = Math.min((float) maxDimension / width, (float) maxDimension / height);
            if (scale < 1.0f) {
                bitmap = Bitmap.createScaledBitmap(bitmap, (int)(width * scale), (int)(height * scale), false);
            }

            int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

            RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            java.util.Hashtable<com.google.zxing.DecodeHintType, Object> hints = new java.util.Hashtable<>();
            hints.put(com.google.zxing.DecodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(com.google.zxing.DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(com.google.zxing.DecodeHintType.POSSIBLE_FORMATS, java.util.Collections.singletonList(com.google.zxing.BarcodeFormat.QR_CODE));

            Result result = new MultiFormatReader().decode(binaryBitmap, hints);
            Log.d("ScanActivity", "Scanned QR from gallery: " + result.getText());
            handleScanResult(result.getText());

        } catch (Exception e) {
            Log.e("ScanActivity", "decodeQRFromImage failed", e);
            Toast.makeText(this, "QR tidak dapat dibaca dari gambar", Toast.LENGTH_SHORT).show();
            restartScanner();
        }
    }

    /** Handle scanned QR **/
    /** Handle scanned QR **/
    private void handleScanResult(String qrText) {
        Log.d("ScanActivity", "QR Scanned raw: " + qrText);

        if (qrText == null || qrText.isEmpty() || isProcessing) return;

        String qrCode = qrText;

        // Jika QR berupa JSON, ambil "kode"
        if (qrText.startsWith("{")) {
            try {
                org.json.JSONObject obj = new org.json.JSONObject(qrText);
                qrCode = obj.optString("kode", qrText);
            } catch (org.json.JSONException e) {
                qrCode = qrText; // fallback
            }
        }

        Log.d("ScanActivity", "QR Scanned parsed: " + qrCode);

        isProcessing = true;
        stopScanner();
        verifyQRCode(qrCode);
    }


    /** Verify QR dengan API **/
    private void verifyQRCode(String qrCode) {
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            isProcessing = false;
            Toast.makeText(this, "User tidak teridentifikasi", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        AttendanceRequest request = new AttendanceRequest("verify_qr", userId, qrCode);
        ApiClient.getApiService().verifyQRCode(request).enqueue(new Callback<ApiResponseSingle<AttendanceData>>() {
            @Override
            public void onResponse(Call<ApiResponseSingle<AttendanceData>> call, Response<ApiResponseSingle<AttendanceData>> response) {
                isProcessing = false;
                if (response.isSuccessful() && response.body() != null) {
                    AttendanceData data = response.body().getData();
                    String msg = (data != null && data.message != null) ? data.message : "QR tidak valid";
                    if (data != null && data.is_valid) openConfirmationActivity(qrCode, data);
                    else {
                        Toast.makeText(ScanActivity.this, msg, Toast.LENGTH_LONG).show();
                        restartScanner();
                    }
                } else {
                    Toast.makeText(ScanActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                    restartScanner();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseSingle<AttendanceData>> call, Throwable t) {
                isProcessing = false;
                Toast.makeText(ScanActivity.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
                restartScanner();
            }
        });
    }

    /** Open confirmation activity **/
    private void openConfirmationActivity(String qrCode, AttendanceData data) {
        Intent intent = new Intent(this, AttendanceConfirmationActivity.class);
        intent.putExtra("qr_code", qrCode);
        intent.putExtra("event_name", data.event_name);
        intent.putExtra("event_date", data.event_date);
        intent.putExtra("event_time", data.event_time);
        intent.putExtra("location", data.location);
        intent.putExtra("organization_name", data.organization_name);
        intent.putExtra("location_required", data.location_required ? "1" : "0");
        intent.putExtra("user_id", data.user_id); // ganti anggota_id jadi user_id
        attendanceLauncher.launch(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (codeScanner != null && !isProcessing) restartScanner();
    }

    @Override
    protected void onPause() {
        stopScanner();
        super.onPause();
    }
}

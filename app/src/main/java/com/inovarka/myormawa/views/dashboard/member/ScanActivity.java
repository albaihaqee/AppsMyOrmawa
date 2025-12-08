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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.ApiResponseSingle;
import com.inovarka.myormawa.models.AttendanceData;
import com.inovarka.myormawa.models.AttendanceRequest;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Collections;
import java.util.Hashtable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends AppCompatActivity {

    private static final String TAG = "ScanActivity";
    private static final int MAX_IMAGE_DIMENSION = 1000;

    // Views
    private CodeScannerView scannerView;
    private ImageView btnBack;
    private ImageView btnFlash;
    private MaterialButton btnGallery;

    // Scanner & State
    private CodeScanner codeScanner;
    private SessionManager sessionManager;
    private boolean isFlashOn = false;
    private boolean isProcessing = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // ===================== ACTIVITY RESULT LAUNCHERS =====================

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    setupScanner();
                } else {
                    Toast.makeText(this, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    private final ActivityResultLauncher<String> requestStoragePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchGallery();
                } else {
                    Toast.makeText(this, "Izin galeri diperlukan", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        decodeQRFromImage(uri);
                    }
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

    // ===================== LIFECYCLE =====================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        setupStatusBar();
        initViews();
        setupClickListeners();
        checkCameraPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (codeScanner != null && !isProcessing) {
            restartScanner();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScanner();
    }

    // ===================== INITIALIZATION =====================

    private void setupStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.md_theme_light_surfaceVariant));

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(false);
    }

    private void initViews() {
        sessionManager = new SessionManager(this);

        scannerView = findViewById(R.id.scanner_view);
        btnBack = findViewById(R.id.btn_back);
        btnFlash = findViewById(R.id.btn_flash);
        btnGallery = findViewById(R.id.btn_upload_qr);
    }

    private void setupClickListeners() {
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
            if (codeScanner != null && !isProcessing) {
                restartScanner();
            }
        });
    }

    // ===================== CAMERA PERMISSION =====================

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            setupScanner();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    // ===================== STORAGE PERMISSION =====================

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

    // ===================== CAMERA SCANNER =====================

    private void setupScanner() {
        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setDecodeCallback(result ->
                mainHandler.post(() -> {
                    if (!isProcessing) {
                        handleScanResult(result.getText());
                    }
                })
        );
        codeScanner.startPreview();
    }

    private void restartScanner() {
        if (codeScanner != null && !isProcessing) {
            codeScanner.startPreview();
        }
    }

    private void stopScanner() {
        if (codeScanner != null) {
            codeScanner.releaseResources();
        }
    }

    private void toggleFlash() {
        if (codeScanner == null) return;

        isFlashOn = !isFlashOn;
        codeScanner.setFlashEnabled(isFlashOn);
        btnFlash.setImageResource(isFlashOn ? R.drawable.ic_flash_on : R.drawable.ic_flash_off);
    }

    // ===================== GALLERY =====================

    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void decodeQRFromImage(Uri uri) {
        try {
            InputStream stream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);

            if (bitmap == null) {
                Toast.makeText(this, "Gagal membaca gambar", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert to RGB format
            bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);

            // Resize if too large
            bitmap = resizeBitmap(bitmap, MAX_IMAGE_DIMENSION);

            // Extract pixels
            int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

            // Create luminance source
            RGBLuminanceSource source = new RGBLuminanceSource(
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    pixels
            );
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            // Set decode hints
            Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, Collections.singletonList(
                    com.google.zxing.BarcodeFormat.QR_CODE
            ));

            // Decode QR
            Result result = new MultiFormatReader().decode(binaryBitmap, hints);
            Log.d(TAG, "QR decoded from gallery: " + result.getText());
            handleScanResult(result.getText());

        } catch (Exception e) {
            Log.e(TAG, "Failed to decode QR from image", e);
            Toast.makeText(this, "QR tidak dapat dibaca dari gambar", Toast.LENGTH_SHORT).show();
            restartScanner();
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxDimension) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scale = Math.min((float) maxDimension / width, (float) maxDimension / height);

        if (scale < 1.0f) {
            int newWidth = (int) (width * scale);
            int newHeight = (int) (height * scale);
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        }

        return bitmap;
    }

    // ===================== QR HANDLING =====================

    private void handleScanResult(String qrText) {
        Log.d(TAG, "QR scanned raw: " + qrText);

        if (qrText == null || qrText.isEmpty() || isProcessing) {
            return;
        }

        String qrCode = parseQRCode(qrText);
        Log.d(TAG, "QR parsed: " + qrCode);

        isProcessing = true;
        stopScanner();
        verifyQRCode(qrCode);
    }

    private String parseQRCode(String qrText) {
        // If QR is JSON format, extract "kode" field
        if (qrText.startsWith("{")) {
            try {
                JSONObject obj = new JSONObject(qrText);
                return obj.optString("kode", qrText);
            } catch (JSONException e) {
                Log.w(TAG, "Failed to parse JSON QR", e);
                return qrText; // Fallback to raw text
            }
        }
        return qrText;
    }

    // ===================== API VERIFICATION =====================

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
            public void onResponse(Call<ApiResponseSingle<AttendanceData>> call,
                                   Response<ApiResponseSingle<AttendanceData>> response) {
                isProcessing = false;

                if (response.isSuccessful() && response.body() != null) {
                    handleVerificationSuccess(response.body(), qrCode);
                } else {
                    Toast.makeText(ScanActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                    restartScanner();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseSingle<AttendanceData>> call, Throwable t) {
                isProcessing = false;
                Log.e(TAG, "Verification failed", t);
                Toast.makeText(ScanActivity.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
                restartScanner();
            }
        });
    }

    private void handleVerificationSuccess(ApiResponseSingle<AttendanceData> responseBody, String qrCode) {
        AttendanceData data = responseBody.getData();

        if (data == null) {
            Toast.makeText(this, "Data tidak valid", Toast.LENGTH_SHORT).show();
            restartScanner();
            return;
        }

        String message = data.message != null ? data.message : "QR tidak valid";

        if (data.is_valid) {
            openConfirmationActivity(qrCode, data);
        } else {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            restartScanner();
        }
    }

    // ===================== NAVIGATION =====================

    private void openConfirmationActivity(String qrCode, AttendanceData data) {
        Intent intent = new Intent(this, AttendanceConfirmationActivity.class);
        intent.putExtra("qr_code", qrCode);
        intent.putExtra("event_name", data.event_name);
        intent.putExtra("event_date", data.event_date);
        intent.putExtra("event_time", data.event_time);
        intent.putExtra("location", data.location);
        intent.putExtra("organization_name", data.organization_name);
        intent.putExtra("location_required", data.location_required ? "1" : "0");
        intent.putExtra("user_id", data.user_id);

        attendanceLauncher.launch(intent);
    }
}
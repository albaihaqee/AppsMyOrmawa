package com.inovarka.myormawa.views.dashboard.member;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.inovarka.myormawa.R;

import java.io.InputStream;

public class ScanActivity extends AppCompatActivity {

    private CodeScanner codeScanner;
    private CodeScannerView scannerView;
    private ImageView btnBack, btnFlash;
    private MaterialButton btnUploadQr;
    private boolean isFlashOn = false;

    // Permission Launchers - menggunakan ActivityResultLauncher (modern way)
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    setupCodeScanner();
                } else {
                    Toast.makeText(this, "Izin kamera diperlukan untuk scan QR", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    private final ActivityResultLauncher<String> requestStoragePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchGalleryIntent();
                } else {
                    Toast.makeText(this, "Izin galeri diperlukan untuk upload QR", Toast.LENGTH_SHORT).show();
                }
            });

    // Gallery Picker Launcher
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        decodeQRFromImage(imageUri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransparentStatusBar();
        setContentView(R.layout.activity_scan);

        initViews();
        setupClickListeners();
        checkCameraPermission();
    }

    private void setTransparentStatusBar() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
    }

    private void initViews() {
        scannerView = findViewById(R.id.scanner_view);
        btnBack = findViewById(R.id.btn_back);
        btnFlash = findViewById(R.id.btn_flash);
        btnUploadQr = findViewById(R.id.btn_upload_qr);
    }

    private void setupClickListeners() {
        // Back button: kembali ke fragment terakhir
        btnBack.setOnClickListener(v -> finish());

        // Toggle flash camera
        btnFlash.setOnClickListener(v -> toggleFlash());

        // Upload QR: check permission lalu buka galeri
        btnUploadQr.setOnClickListener(v -> checkStoragePermission());
    }

    // Check camera permission saat pertama kali buka
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            setupCodeScanner();
        } else {
            // Akan muncul dialog system "Allow" / "Deny" / "Only this time"
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    // Check storage permission saat klik upload
    private void checkStoragePermission() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED) {
            launchGalleryIntent();
        } else {
            // Akan muncul dialog system "Allow" / "Deny" / "Only this time"
            requestStoragePermissionLauncher.launch(permission);
        }
    }

    // Setup code scanner untuk scan QR real-time
    private void setupCodeScanner() {
        codeScanner = new CodeScanner(this, scannerView);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(() -> handleScanResult(result.getText()));
            }
        });

        scannerView.setOnClickListener(view -> codeScanner.startPreview());
        codeScanner.startPreview();
    }

    // Toggle flash on/off
    private void toggleFlash() {
        if (codeScanner != null) {
            if (isFlashOn) {
                codeScanner.setFlashEnabled(false);
                btnFlash.setImageResource(R.drawable.ic_flash_off);
                isFlashOn = false;
            } else {
                codeScanner.setFlashEnabled(true);
                btnFlash.setImageResource(R.drawable.ic_flash_on);
                isFlashOn = true;
            }
        }
    }

    // Buka galeri untuk pilih gambar
    private void launchGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    // Decode QR Code dari gambar yang dipilih
    private void decodeQRFromImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap != null) {
                int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
                bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

                RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

                MultiFormatReader reader = new MultiFormatReader();
                Result result = reader.decode(binaryBitmap);

                handleScanResult(result.getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal membaca QR Code dari gambar", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle hasil scan QR (dari kamera atau upload)
    private void handleScanResult(String qrContent) {
        Toast.makeText(this, "QR Code: " + qrContent, Toast.LENGTH_LONG).show();

        // TODO: Proses QR Code (kirim ke server, validasi, dll)

        // Kirim hasil ke DashboardMemberActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("qr_result", qrContent);
        setResult(RESULT_OK, resultIntent);

        // Kembali ke fragment terakhir
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (codeScanner != null) {
            codeScanner.startPreview();
        }
    }

    @Override
    protected void onPause() {
        if (codeScanner != null) {
            codeScanner.releaseResources();
        }
        super.onPause();
    }
}
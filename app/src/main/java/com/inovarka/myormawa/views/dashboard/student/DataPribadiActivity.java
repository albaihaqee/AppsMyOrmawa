package com.inovarka.myormawa.views.dashboard.student;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.inovarka.myormawa.R;

public class DataPribadiActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyOrmawaPrefs";
    private static final int PERMISSION_CODE = 100;

    private ImageView btnBack;
    private TextView txtAvatar;
    private TextInputEditText edtNamaLengkap, edtNim, edtEmail, edtProgramStudi, edtAngkatan;
    private TextInputLayout tilEmail;
    private View btnChangePhoto;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_data_pribadi);

        initViews();
        setupLaunchers();
        loadUserData();
        setupListeners();
    }

    private void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        txtAvatar = findViewById(R.id.txt_avatar);
        edtNamaLengkap = findViewById(R.id.edt_nama_lengkap);
        edtNim = findViewById(R.id.edt_nim);
        edtEmail = findViewById(R.id.edt_email);
        edtProgramStudi = findViewById(R.id.edt_program_studi);
        edtAngkatan = findViewById(R.id.edt_angkatan);
        tilEmail = findViewById(R.id.til_email);
        btnChangePhoto = findViewById(R.id.btn_change_photo);
    }

    private void setupLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(this, "Foto berhasil diambil", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Toast.makeText(this, "Foto berhasil dipilih", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String fullName = prefs.getString("full_name", "Hilmi Madani");
        String nim = prefs.getString("nim", "E41242025");
        String email = prefs.getString("email", "e41242025@student.polije.ac.id");
        String prodi = prefs.getString("program_studi", "Teknik Informatika");
        String angkatan = prefs.getString("angkatan", "2024");

        edtNamaLengkap.setText(fullName);
        edtNim.setText(nim);
        edtEmail.setText(email);
        edtProgramStudi.setText(prodi);
        edtAngkatan.setText(angkatan);

        txtAvatar.setText(getInitials(fullName));
    }

    private String getInitials(String name) {
        String[] parts = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                initials.append(part.charAt(0));
            }
        }
        return initials.toString().toUpperCase();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnChangePhoto.setOnClickListener(v -> showImageDialog());
        tilEmail.setEndIconOnClickListener(v -> showEmailDialog());
    }

    private void showImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Sumber Foto");

        String[] options = {"Ambil Foto", "Pilih dari Galeri", "Batal"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    if (checkPermissions()) openCamera();
                    break;
                case 1:
                    if (checkPermissions()) openGallery();
                    break;
                case 2:
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    private boolean checkPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);
                return false;
            }
        }
        return true;
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            galleryLauncher.launch(intent);
        }
    }

    private void showEmailDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_email);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
            window.setGravity(Gravity.CENTER);
        }

        MaterialButton btnCancel = dialog.findViewById(R.id.btn_cancel);
        MaterialButton btnContinue = dialog.findViewById(R.id.btn_continue);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnContinue.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(this, ChangeEmailActivity.class));
        });

        dialog.show();
    }
}
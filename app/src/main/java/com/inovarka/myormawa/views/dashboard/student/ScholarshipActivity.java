package com.inovarka.myormawa.views.dashboard.student;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.ScholarshipAdapter;
import com.inovarka.myormawa.models.Scholarship;
import com.inovarka.myormawa.repositories.ScholarshipRepository;

import java.util.List;

public class ScholarshipActivity extends AppCompatActivity {

    private static final String TAG = "ScholarshipActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;

    private RecyclerView rvScholarships;
    private ScholarshipAdapter adapter;
    private ScholarshipRepository scholarshipRepository;
    private LinearLayout emptyStateView;
    private LinearLayout loadingView;

    private Scholarship pendingDownloadScholarship; // Untuk menyimpan scholarship yang akan didownload

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_scholarship);

        initViews();
        setupRecyclerView();
        loadScholarships();
    }

    private void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        rvScholarships = findViewById(R.id.rv_scholarships);
        loadingView = findViewById(R.id.loading_view);
        emptyStateView = findViewById(R.id.empty_state_view);

        scholarshipRepository = new ScholarshipRepository();
    }

    private void setupRecyclerView() {
        adapter = new ScholarshipAdapter();
        rvScholarships.setLayoutManager(new LinearLayoutManager(this));
        rvScholarships.setAdapter(adapter);

        adapter.setOnItemClickListener(this::showScholarshipDetailDialog);
    }

    private void loadScholarships() {
        showLoading();

        scholarshipRepository.getUpcomingScholarships().observe(this, new Observer<List<Scholarship>>() {
            @Override
            public void onChanged(List<Scholarship> scholarships) {
                hideLoading();

                if (scholarships != null && !scholarships.isEmpty()) {
                    Log.d(TAG, "Scholarships loaded: " + scholarships.size());

                    // Debug log untuk cek data
                    for (Scholarship scholarship : scholarships) {
                        Log.d(TAG, "Scholarship: " + scholarship.getTitle());
                        Log.d(TAG, "Poster URL: " + scholarship.getPosterUrl());
                        Log.d(TAG, "GuideBook URL: " + scholarship.getGuideBookUrl());
                    }

                    adapter.setScholarshipList(scholarships);
                    showContent();
                } else {
                    Log.d(TAG, "No scholarships found");
                    showEmptyState();
                }
            }
        });
    }

    private void showScholarshipDetailDialog(Scholarship scholarship) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_scholarship_detail, null);

        ImageView btnClose = view.findViewById(R.id.btn_close_dialog);
        TextView txtTitle = view.findViewById(R.id.txt_scholarship_detail_title);
        TextView txtProvider = view.findViewById(R.id.txt_scholarship_detail_provider);
        TextView txtDeadline = view.findViewById(R.id.txt_scholarship_detail_deadline);
        TextView txtDescription = view.findViewById(R.id.txt_scholarship_detail_description);
        View btnDownload = view.findViewById(R.id.btn_download_guidebook);

        txtTitle.setText(scholarship.getTitle());
        txtProvider.setText(scholarship.getProvider());
        txtDeadline.setText(scholarship.getDeadline());
        txtDescription.setText(scholarship.getDescription());

        // Handle download button
        if (scholarship.hasGuideBook()) {
            btnDownload.setVisibility(View.VISIBLE);
            btnDownload.setOnClickListener(v -> {
                dialog.dismiss();
                checkPermissionAndDownload(scholarship);
            });
        } else {
            btnDownload.setVisibility(View.GONE);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(view);
        dialog.show();
    }

    private void checkPermissionAndDownload(Scholarship scholarship) {
        pendingDownloadScholarship = scholarship;

        // Android 10+ tidak perlu permission WRITE_EXTERNAL_STORAGE untuk Downloads
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            downloadGuideBook(scholarship);
        } else {
            // Android 9 dan dibawah perlu permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                downloadGuideBook(scholarship);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingDownloadScholarship != null) {
                    downloadGuideBook(pendingDownloadScholarship);
                }
            } else {
                Toast.makeText(this, "Izin storage diperlukan untuk download", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void downloadGuideBook(Scholarship scholarship) {
        String guideBookUrl = scholarship.getGuideBookUrl();

        if (guideBookUrl == null || guideBookUrl.isEmpty()) {
            Toast.makeText(this, "URL buku panduan tidak tersedia", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "GuideBook URL is null or empty");
            return;
        }

        // Validasi URL
        if (!guideBookUrl.startsWith("http://") && !guideBookUrl.startsWith("https://")) {
            Toast.makeText(this, "URL buku panduan tidak valid", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Invalid URL: " + guideBookUrl);
            return;
        }

        try {
            Log.d(TAG, "Starting download from: " + guideBookUrl);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(guideBookUrl));

            // Set notification
            request.setTitle("Buku Panduan Beasiswa");
            request.setDescription("Mengunduh " + scholarship.getTitle());
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            // Set file name
            String fileName = scholarship.getGuideBookFilename();
            if (fileName == null || fileName.isEmpty()) {
                fileName = "BukuPanduan_Beasiswa_" + scholarship.getId() + ".pdf";
            }

            Log.d(TAG, "File name: " + fileName);

            // Set destination (Downloads folder)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            // Allow scanning by media scanner
            request.allowScanningByMediaScanner();

            // Set MIME type
            request.setMimeType("application/pdf");

            // Allow download over mobile data and WiFi
            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI
            );

            // Get download manager
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

            if (downloadManager != null) {
                long downloadId = downloadManager.enqueue(request);
                Log.d(TAG, "Download started with ID: " + downloadId);
                Toast.makeText(this, "Mengunduh buku panduan...", Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "DownloadManager is null");
                Toast.makeText(this, "Gagal memulai download", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Download error: " + e.getMessage(), e);
            Toast.makeText(this, "Gagal mengunduh: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showLoading() {
        if (loadingView != null) loadingView.setVisibility(View.VISIBLE);
        if (rvScholarships != null) rvScholarships.setVisibility(View.GONE);
        if (emptyStateView != null) emptyStateView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (loadingView != null) loadingView.setVisibility(View.GONE);
    }

    private void showContent() {
        if (rvScholarships != null) rvScholarships.setVisibility(View.VISIBLE);
        if (emptyStateView != null) emptyStateView.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        if (rvScholarships != null) rvScholarships.setVisibility(View.GONE);
        if (emptyStateView != null) emptyStateView.setVisibility(View.VISIBLE);
    }
}
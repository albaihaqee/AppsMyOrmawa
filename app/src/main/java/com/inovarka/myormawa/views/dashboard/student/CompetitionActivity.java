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
import com.inovarka.myormawa.adapters.CompetitionAdapter;
import com.inovarka.myormawa.models.Competition;
import com.inovarka.myormawa.repositories.CompetitionRepository;

import java.util.List;

public class CompetitionActivity extends AppCompatActivity {

    private static final String TAG = "CompetitionActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;

    private RecyclerView rvCompetitions;
    private CompetitionAdapter adapter;
    private CompetitionRepository competitionRepository;
    private LinearLayout emptyStateView;
    private LinearLayout loadingView;

    private Competition pendingDownloadCompetition; // Untuk menyimpan competition yang akan didownload

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_competition);

        initViews();
        setupRecyclerView();
        loadCompetitions();
    }

    private void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        rvCompetitions = findViewById(R.id.rv_competitions);
        loadingView = findViewById(R.id.loading_view);
        emptyStateView = findViewById(R.id.empty_state_view);

        competitionRepository = new CompetitionRepository();
    }

    private void setupRecyclerView() {
        adapter = new CompetitionAdapter();
        rvCompetitions.setLayoutManager(new LinearLayoutManager(this));
        rvCompetitions.setAdapter(adapter);

        adapter.setOnItemClickListener(this::showCompetitionDetailDialog);
    }

    private void loadCompetitions() {
        showLoading();

        competitionRepository.getUpcomingCompetitions().observe(this, new Observer<List<Competition>>() {
            @Override
            public void onChanged(List<Competition> competitions) {
                hideLoading();

                if (competitions != null && !competitions.isEmpty()) {
                    Log.d(TAG, "Competitions loaded: " + competitions.size());

                    // Debug log untuk cek data
                    for (Competition competition : competitions) {
                        Log.d(TAG, "Competition: " + competition.getTitle());
                        Log.d(TAG, "Poster URL: " + competition.getPosterUrl());
                        Log.d(TAG, "GuideBook URL: " + competition.getGuideBookUrl());
                    }

                    adapter.setCompetitionList(competitions);
                    showContent();
                } else {
                    Log.d(TAG, "No competitions found");
                    showEmptyState();
                }
            }
        });
    }

    private void showCompetitionDetailDialog(Competition competition) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_competition_detail, null);

        ImageView btnClose = view.findViewById(R.id.btn_close_dialog);
        TextView txtTitle = view.findViewById(R.id.txt_competition_detail_title);
        TextView txtOrganizer = view.findViewById(R.id.txt_competition_detail_organizer);
        TextView txtPeriod = view.findViewById(R.id.txt_competition_detail_period);
        TextView txtDescription = view.findViewById(R.id.txt_competition_detail_description);
        View btnDownload = view.findViewById(R.id.btn_download_guidebook);

        txtTitle.setText(competition.getTitle());
        txtOrganizer.setText(competition.getOrganizer());
        txtPeriod.setText(competition.getRegistrationPeriod());
        txtDescription.setText(competition.getDescription());

        // Handle download button
        if (competition.hasGuideBook()) {
            btnDownload.setVisibility(View.VISIBLE);
            btnDownload.setOnClickListener(v -> {
                dialog.dismiss();
                checkPermissionAndDownload(competition);
            });
        } else {
            btnDownload.setVisibility(View.GONE);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(view);
        dialog.show();
    }

    private void checkPermissionAndDownload(Competition competition) {
        pendingDownloadCompetition = competition;

        // Android 10+ tidak perlu permission WRITE_EXTERNAL_STORAGE untuk Downloads
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            downloadGuideBook(competition);
        } else {
            // Android 9 dan dibawah perlu permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                downloadGuideBook(competition);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingDownloadCompetition != null) {
                    downloadGuideBook(pendingDownloadCompetition);
                }
            } else {
                Toast.makeText(this, "Izin storage diperlukan untuk download", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void downloadGuideBook(Competition competition) {
        String guideBookUrl = competition.getGuideBookUrl();

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
            request.setTitle("Buku Panduan Kompetisi");
            request.setDescription("Mengunduh " + competition.getTitle());
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            // Set file name
            String fileName = competition.getGuideBookFilename();
            if (fileName == null || fileName.isEmpty()) {
                fileName = "BukuPanduan_Kompetisi_" + competition.getId() + ".pdf";
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
        if (rvCompetitions != null) rvCompetitions.setVisibility(View.GONE);
        if (emptyStateView != null) emptyStateView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (loadingView != null) loadingView.setVisibility(View.GONE);
    }

    private void showContent() {
        if (rvCompetitions != null) rvCompetitions.setVisibility(View.VISIBLE);
        if (emptyStateView != null) emptyStateView.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        if (rvCompetitions != null) rvCompetitions.setVisibility(View.GONE);
        if (emptyStateView != null) emptyStateView.setVisibility(View.VISIBLE);
    }
}
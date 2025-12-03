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
import com.inovarka.myormawa.adapters.EventAdapter;
import com.inovarka.myormawa.models.Event;
import com.inovarka.myormawa.repositories.EventRepository;

import java.util.List;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = "EventActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;

    private RecyclerView rvEvents;
    private EventAdapter adapter;
    private EventRepository eventRepository;
    private LinearLayout emptyStateView;
    private LinearLayout loadingView;

    private Event pendingDownloadEvent; // Untuk menyimpan event yang akan didownload

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_event);

        initViews();
        setupRecyclerView();
        loadEvents();
    }

    private void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        rvEvents = findViewById(R.id.rv_events);
        loadingView = findViewById(R.id.loading_view);
        emptyStateView = findViewById(R.id.empty_state_view);

        eventRepository = new EventRepository();
    }

    private void setupRecyclerView() {
        adapter = new EventAdapter();
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(adapter);

        adapter.setOnItemClickListener(this::showEventDetailDialog);
    }

    private void loadEvents() {
        showLoading();

        eventRepository.getUpcomingEvents().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                hideLoading();

                if (events != null && !events.isEmpty()) {
                    Log.d(TAG, "Events loaded: " + events.size());

                    // Debug log untuk cek data
                    for (Event event : events) {
                        Log.d(TAG, "Event: " + event.getTitle());
                        Log.d(TAG, "Poster URL: " + event.getPosterUrl());
                        Log.d(TAG, "GuideBook URL: " + event.getGuideBookUrl());
                    }

                    adapter.setEventList(events);
                    showContent();
                } else {
                    Log.d(TAG, "No events found");
                    showEmptyState();
                }
            }
        });
    }

    private void showEventDetailDialog(Event event) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_event_detail, null);

        ImageView btnClose = view.findViewById(R.id.btn_close_dialog);
        TextView txtTitle = view.findViewById(R.id.txt_event_detail_title);
        TextView txtOrganizer = view.findViewById(R.id.txt_event_detail_organizer);
        TextView txtLocation = view.findViewById(R.id.txt_event_detail_location);
        TextView txtDate = view.findViewById(R.id.txt_event_detail_date);
        TextView txtDescription = view.findViewById(R.id.txt_event_detail_description);
        View btnDownload = view.findViewById(R.id.btn_download_guidebook);

        LinearLayout layoutTimeSection = view.findViewById(R.id.layout_time_section);
        TextView txtTime = view.findViewById(R.id.txt_event_detail_time);

        txtTitle.setText(event.getTitle());
        txtOrganizer.setText(event.getOrganizer());
        txtLocation.setText(event.getLocation());
        txtDate.setText(event.getDate());
        txtDescription.setText(event.getDescription());

        if (event.hasTimeInfo()) {
            layoutTimeSection.setVisibility(View.VISIBLE);
            txtTime.setText(event.getWaktuLengkap());
        } else {
            layoutTimeSection.setVisibility(View.GONE);
        }

        // Handle download button
        if (event.hasGuideBook()) {
            btnDownload.setVisibility(View.VISIBLE);
            btnDownload.setOnClickListener(v -> {
                dialog.dismiss();
                checkPermissionAndDownload(event);
            });
        } else {
            btnDownload.setVisibility(View.GONE);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(view);
        dialog.show();
    }

    private void checkPermissionAndDownload(Event event) {
        pendingDownloadEvent = event;

        // Android 10+ tidak perlu permission WRITE_EXTERNAL_STORAGE untuk Downloads
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            downloadGuideBook(event);
        } else {
            // Android 9 dan dibawah perlu permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                downloadGuideBook(event);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingDownloadEvent != null) {
                    downloadGuideBook(pendingDownloadEvent);
                }
            } else {
                Toast.makeText(this, "Izin storage diperlukan untuk download", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void downloadGuideBook(Event event) {
        String guideBookUrl = event.getGuideBookUrl();

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
            request.setTitle("Buku Panduan Event");
            request.setDescription("Mengunduh " + event.getTitle());
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            // Set file name
            String fileName = event.getGuideBookFilename();
            if (fileName == null || fileName.isEmpty()) {
                fileName = "BukuPanduan_" + event.getId() + ".pdf";
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
        if (rvEvents != null) rvEvents.setVisibility(View.GONE);
        if (emptyStateView != null) emptyStateView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (loadingView != null) loadingView.setVisibility(View.GONE);
    }

    private void showContent() {
        if (rvEvents != null) rvEvents.setVisibility(View.VISIBLE);
        if (emptyStateView != null) emptyStateView.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        if (rvEvents != null) rvEvents.setVisibility(View.GONE);
        if (emptyStateView != null) emptyStateView.setVisibility(View.VISIBLE);
    }
}
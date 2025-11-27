package com.inovarka.myormawa.views.dashboard.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.ScholarshipAdapter;
import com.inovarka.myormawa.models.Scholarship;

import java.util.ArrayList;
import java.util.List;

public class ScholarshipActivity extends AppCompatActivity {

    private RecyclerView rvScholarships;
    private ScholarshipAdapter adapter;
    private List<Scholarship> scholarshipList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_scholarship);

        initViews();
        setupRecyclerView();
        loadDummyData();
    }

    private void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        rvScholarships = findViewById(R.id.rv_scholarships);
    }

    private void setupRecyclerView() {
        adapter = new ScholarshipAdapter();
        rvScholarships.setLayoutManager(new LinearLayoutManager(this));
        rvScholarships.setAdapter(adapter);

        adapter.setOnItemClickListener(this::showScholarshipDetailDialog);
    }

    private void showScholarshipDetailDialog(Scholarship scholarship) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_scholarship_detail, null);

        ImageView btnClose = view.findViewById(R.id.btn_close_dialog);
        TextView txtTitle = view.findViewById(R.id.txt_scholarship_detail_title);
        TextView txtProvider = view.findViewById(R.id.txt_scholarship_detail_provider);
        TextView txtAmount = view.findViewById(R.id.txt_scholarship_detail_amount);
        TextView txtDeadline = view.findViewById(R.id.txt_scholarship_detail_deadline);
        TextView txtGPA = view.findViewById(R.id.txt_scholarship_detail_gpa);
        TextView txtDescription = view.findViewById(R.id.txt_scholarship_detail_description);
        View btnDownload = view.findViewById(R.id.btn_download_guidebook);

        txtTitle.setText(scholarship.getTitle());
        txtProvider.setText(scholarship.getProvider());
        txtAmount.setText(scholarship.getAmount());
        txtDeadline.setText(scholarship.getDeadline());
        txtGPA.setText(scholarship.getMinGPAText());
        txtDescription.setText(scholarship.getDescription());

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnDownload.setOnClickListener(v -> {
            Toast.makeText(this, "Mengunduh buku panduan...", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void loadDummyData() {
        scholarshipList = new ArrayList<>();

        scholarshipList.add(new Scholarship(
                "1",
                "Beasiswa PPA 2025",
                "Kementrian Pendidikan",
                "Rp 4.000.000/semester",
                "30 November 2025",
                3.00,
                "https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=400&h=533&fit=crop",
                "Beasiswa Peningkatan Prestasi Akademik untuk mahasiswa berprestasi dengan kondisi ekonomi kurang mampu.",
                ""
        ));

        scholarshipList.add(new Scholarship(
                "2",
                "Beasiswa Unggulan",
                "Kementrian Pendidikan",
                "Full Kuliah",
                "15 Desember 2025",
                3.50,
                "https://images.unsplash.com/photo-1541339907198-e08756dedf3f?w=400&h=533&fit=crop",
                "Beasiswa unggulan untuk mahasiswa dengan prestasi akademik dan non-akademik yang luar biasa.",
                ""
        ));

        scholarshipList.add(new Scholarship(
                "3",
                "Beasiswa BCA",
                "Bank BCA",
                "Rp 5.000.000/semester",
                "20 Desember 2025",
                3.25,
                "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400&h=533&fit=crop",
                "Beasiswa dari Bank BCA untuk mahasiswa berprestasi di bidang teknologi.",
                ""
        ));

        adapter.setScholarshipList(scholarshipList);
    }
}
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
import com.inovarka.myormawa.adapters.CompetitionAdapter;
import com.inovarka.myormawa.models.Competition;

import java.util.ArrayList;
import java.util.List;

public class CompetitionActivity extends AppCompatActivity {

    private RecyclerView rvCompetitions;
    private CompetitionAdapter adapter;
    private List<Competition> competitionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_competition);

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

        rvCompetitions = findViewById(R.id.rv_competitions);
    }

    private void setupRecyclerView() {
        adapter = new CompetitionAdapter();
        rvCompetitions.setLayoutManager(new LinearLayoutManager(this));
        rvCompetitions.setAdapter(adapter);

        adapter.setOnItemClickListener(this::showCompetitionDetailDialog);
    }

    private void showCompetitionDetailDialog(Competition competition) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_competition_detail, null);

        ImageView btnClose = view.findViewById(R.id.btn_close_dialog);
        TextView txtTitle = view.findViewById(R.id.txt_competition_detail_title);
        TextView txtOrganizer = view.findViewById(R.id.txt_competition_detail_organizer);
        TextView txtPeriod = view.findViewById(R.id.txt_competition_detail_period);
        TextView txtDeadline = view.findViewById(R.id.txt_competition_detail_deadline);
        TextView txtPrize = view.findViewById(R.id.txt_competition_detail_prize);
        TextView txtTeam = view.findViewById(R.id.txt_competition_detail_team);
        TextView txtDescription = view.findViewById(R.id.txt_competition_detail_description);
        View btnDownload = view.findViewById(R.id.btn_download_guidebook);

        txtTitle.setText(competition.getTitle());
        txtOrganizer.setText(competition.getOrganizer());
        txtPeriod.setText(competition.getRegistrationPeriod());
        txtDeadline.setText(competition.getDeadline());
        txtPrize.setText(competition.getPrize());
        txtTeam.setText(competition.getTeamSizeText());
        txtDescription.setText(competition.getDescription());

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnDownload.setOnClickListener(v -> {
            Toast.makeText(this, "Mengunduh buku panduan...", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void loadDummyData() {
        competitionList = new ArrayList<>();

        competitionList.add(new Competition(
                "1",
                "Hackathon AI Innovation",
                "HMJ Teknologi Informasi",
                "10-12 Januari 2026",
                "5 Januari 2026",
                "Total Hadiah 15 Juta",
                50,
                "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=400&h=533&fit=crop",
                "Kompetisi hackathon bertema AI Innovation untuk mahasiswa se-Indonesia.",
                ""
        ));

        competitionList.add(new Competition(
                "2",
                "Kompetisi Web Programming",
                "Fakultas Teknologi Informasi",
                "15-17 Januari 2026",
                "8 Januari 2026",
                "Total Hadiah 25 Juta",
                50,
                "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=400&h=533&fit=crop",
                "Kompetisi pembuatan website dengan tema Smart City untuk mahasiswa se-Indonesia.",
                ""
        ));

        competitionList.add(new Competition(
                "3",
                "UI/UX Design Competition",
                "Politeknik Negeri Jember",
                "20-22 Januari 2026",
                "13 Januari 2026",
                "Total Hadiah 10 Juta",
                30,
                "https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=400&h=533&fit=crop",
                "Kompetisi desain UI/UX untuk aplikasi mobile.",
                ""
        ));

        adapter.setCompetitionList(competitionList);
    }
}
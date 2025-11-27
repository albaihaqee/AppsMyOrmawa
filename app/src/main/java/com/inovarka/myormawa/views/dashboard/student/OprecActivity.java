package com.inovarka.myormawa.views.dashboard.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.OprecAdapter;
import com.inovarka.myormawa.models.Oprec;

import java.util.ArrayList;
import java.util.List;

public class OprecActivity extends AppCompatActivity {

    private RecyclerView rvOprec;
    private OprecAdapter adapter;
    private ChipGroup chipGroup;
    private List<Oprec> allOprec;
    private List<Oprec> filteredOprec;

    private String currentType = "Pengurus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_oprec);

        initViews();
        setupRecyclerView();
        setupChips();
        loadDummyData();
    }

    private void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        rvOprec = findViewById(R.id.rv_oprec);
        chipGroup = findViewById(R.id.chip_group_oprec);
    }

    private void setupRecyclerView() {
        adapter = new OprecAdapter();
        rvOprec.setLayoutManager(new LinearLayoutManager(this));
        rvOprec.setAdapter(adapter);

        adapter.setOnItemClickListener(this::showOprecDetailDialog);
    }

    private void setupChips() {
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int selectedChipId = checkedIds.get(0);
                Chip selectedChip = findViewById(selectedChipId);
                String type = selectedChip.getText().toString();

                if (type.equals("Oprec Pengurus")) {
                    filterOprec("Pengurus");
                } else {
                    filterOprec("Kepanitiaan");
                }
            }
        });
    }

    private void filterOprec(String type) {
        currentType = type;
        filteredOprec = new ArrayList<>();

        for (Oprec oprec : allOprec) {
            if (oprec.getType().equals(type)) {
                filteredOprec.add(oprec);
            }
        }
        adapter.setOprecList(filteredOprec);
    }

    private void showOprecDetailDialog(Oprec oprec) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_oprec_detail, null);

        ImageView btnClose = view.findViewById(R.id.btn_close_dialog);
        TextView txtTitle = view.findViewById(R.id.txt_oprec_detail_title);
        TextView txtOrganization = view.findViewById(R.id.txt_oprec_detail_organization);
        TextView txtDeadline = view.findViewById(R.id.txt_oprec_detail_deadline);
        TextView txtParticipants = view.findViewById(R.id.txt_oprec_detail_participants);
        TextView txtDescription = view.findViewById(R.id.txt_oprec_detail_description);
        View btnRegister = view.findViewById(R.id.btn_register_now);

        txtTitle.setText(oprec.getTitle());
        txtOrganization.setText(oprec.getOrganization());
        txtDeadline.setText(oprec.getDeadline());
        txtParticipants.setText(oprec.getParticipantsText());
        txtDescription.setText(oprec.getDescription());

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnRegister.setOnClickListener(v -> {
            // TODO: Navigate to form builder
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void loadDummyData() {
        allOprec = new ArrayList<>();

        // Pengurus
        allOprec.add(new Oprec(
                "1",
                "Open Recruitment HMJ-TI",
                "Himpunan Mahasiswa Jurusan Teknologi Informasi",
                "25 November 2025",
                16,
                100,
                "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=400&h=533&fit=crop",
                "Pengurus",
                "Open Recruitment HMJ-TI dibuka untuk mahasiswa yang ingin bergabung dan berkontribusi dalam kepengurusan.",
                true
        ));

        allOprec.add(new Oprec(
                "2",
                "Open Recruitment UKM-O",
                "UKM Kegiatan Mahasiswa Olahraga",
                "30 November 2025",
                25,
                80,
                "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=400&h=533&fit=crop",
                "Pengurus",
                "Bergabunglah dengan UKM-O untuk mengembangkan bakat olahraga.",
                true
        ));

        allOprec.add(new Oprec(
                "3",
                "Open Recruitment UKM PSM",
                "UKM Kegiatan Mahasiswa Paduan Suara",
                "20 November 2025",
                30,
                50,
                "https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?w=400&h=533&fit=crop",
                "Pengurus",
                "UKM PSM membuka pendaftaran anggota baru.",
                true
        ));

        // Kepanitiaan
        allOprec.add(new Oprec(
                "4",
                "Open Recruitment Panitia AOM 11.0",
                "Art of Manajement 11.0",
                "20 November 2025",
                40,
                100,
                "https://images.unsplash.com/photo-1515187029135-18ee286d815b?w=400&h=533&fit=crop",
                "Kepanitiaan",
                "Bergabunglah menjadi panitia AOM 11.0.",
                true
        ));

        allOprec.add(new Oprec(
                "5",
                "Open Recruitment Panitia Inagurasi",
                "HMJ Politeknik Negeri Jember",
                "15 November 2025",
                35,
                90,
                "https://images.unsplash.com/photo-1511578314322-379afb476865?w=400&h=533&fit=crop",
                "Kepanitiaan",
                "Panitia Inagurasi membuka pendaftaran.",
                true
        ));

        allOprec.add(new Oprec(
                "6",
                "Open Recruitment Panitia PKKMB GEMPITA 2025",
                "PKKMB GEMPITA 2025",
                "10 November 2025",
                45,
                100,
                "https://images.unsplash.com/photo-1523580494863-6f3031224c94?w=400&h=533&fit=crop",
                "Kepanitiaan",
                "Menjadi bagian dari panitia PKKMB GEMPITA 2025.",
                true
        ));

        allOprec.add(new Oprec(
                "7",
                "Open Recruitment Panitia AITEC 7",
                "Annual Information Technology Competition 7",
                "18 November 2025",
                28,
                70,
                "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=400&h=533&fit=crop",
                "Kepanitiaan",
                "Bergabung dengan panitia AITEC 7.",
                true
        ));

        filterOprec("Pengurus");
    }
}
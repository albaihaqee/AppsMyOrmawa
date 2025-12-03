package com.inovarka.myormawa.views.dashboard.member;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.DocumentAdapter;
import com.inovarka.myormawa.models.Document;

import java.util.ArrayList;
import java.util.List;

public class DocumentActivity extends AppCompatActivity {

    private RecyclerView rvDocuments;
    private DocumentAdapter documentAdapter;

    private List<Document> fullDocumentList = new ArrayList<>();
    private List<Document> displayDocumentList = new ArrayList<>();

    // Chips
    private Chip chipAll, chipPdf, chipWord, chipExcel, chipPpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        // Views
        ImageView btnBack = findViewById(R.id.btn_back);
        rvDocuments = findViewById(R.id.rv_documents);

        chipAll = findViewById(R.id.chip_all_types);
        chipPdf = findViewById(R.id.chip_pdf);
        chipWord = findViewById(R.id.chip_word);
        chipExcel = findViewById(R.id.chip_excel);
        chipPpt = findViewById(R.id.chip_ppt);

        btnBack.setOnClickListener(v -> finish());

        setupRecyclerView();
        loadDocumentData();
        setupChipListeners();
    }

    private void setupRecyclerView() {
        documentAdapter = new DocumentAdapter(displayDocumentList);
        rvDocuments.setLayoutManager(new LinearLayoutManager(this));
        rvDocuments.setAdapter(documentAdapter);
    }

    private void setupChipListeners() {
        chipAll.setOnClickListener(v -> documentAdapter.updateList(fullDocumentList));

        chipPdf.setOnClickListener(v -> filterDocumentsByType(new String[]{"PDF"}));

        chipWord.setOnClickListener(v -> filterDocumentsByType(new String[]{"DOC", "DOCX"}));

        chipExcel.setOnClickListener(v -> filterDocumentsByType(new String[]{"XLS", "XLSX"}));

        chipPpt.setOnClickListener(v -> filterDocumentsByType(new String[]{"PPT", "PPTX"}));
    }

    private void filterDocumentsByType(String[] types) {
        List<Document> filtered = new ArrayList<>();
        for (Document doc : fullDocumentList) {
            for (String t : types) {
                if (doc.getType().equalsIgnoreCase(t)) {
                    filtered.add(doc);
                }
            }
        }
        documentAdapter.updateList(filtered);
    }

    private void loadDocumentData() {
        fullDocumentList.clear();

        fullDocumentList.add(new Document("1", "Proposal Kegiatan 2024.pdf", "PDF", "25 November 2024", "2.5 MB", ""));
        fullDocumentList.add(new Document("2", "Laporan Keuangan Q4.xlsx", "XLSX", "24 November 2024", "1.8 MB", ""));
        fullDocumentList.add(new Document("3", "Presentasi Project.pptx", "PPTX", "23 November 2024", "5.2 MB", ""));
        fullDocumentList.add(new Document("4", "Surat Keputusan.docx", "DOCX", "22 November 2024", "350 KB", ""));
        fullDocumentList.add(new Document("5", "Panduan Pengguna.pdf", "PDF", "21 November 2024", "1.2 MB", ""));

        displayDocumentList.clear();
        displayDocumentList.addAll(fullDocumentList);

        documentAdapter.notifyDataSetChanged();

        // Default: chip "Semua" sudah checked di XML
    }
}
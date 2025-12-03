package com.inovarka.myormawa.views.dashboard.member;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.DocumentAdapter;
import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.models.Document;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.network.ApiService;
import com.inovarka.myormawa.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentActivity extends AppCompatActivity {

    private RecyclerView rvDocuments;
    private DocumentAdapter documentAdapter;

    private List<Document> fullDocumentList = new ArrayList<>();
    private List<Document> displayDocumentList = new ArrayList<>();

    private Chip chipAll, chipPdf, chipWord, chipExcel, chipPpt;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        sessionManager = new SessionManager(this);

        ImageView btnBack = findViewById(R.id.btn_back);
        rvDocuments = findViewById(R.id.rv_documents);

        chipAll = findViewById(R.id.chip_all_types);
        chipPdf = findViewById(R.id.chip_pdf);
        chipWord = findViewById(R.id.chip_word);
        chipExcel = findViewById(R.id.chip_excel);

        btnBack.setOnClickListener(v -> finish());

        setupRecyclerView();
        setupChipListeners();
        loadDocumentData();
    }

    private void setupRecyclerView() {
        documentAdapter = new DocumentAdapter(this, displayDocumentList);
        rvDocuments.setLayoutManager(new LinearLayoutManager(this));
        rvDocuments.setAdapter(documentAdapter);
    }

    private void setupChipListeners() {
        chipAll.setOnClickListener(v -> documentAdapter.updateList(fullDocumentList));

        chipPdf.setOnClickListener(v -> filterDocumentsByType(new String[]{"PDF"}));
        chipWord.setOnClickListener(v -> filterDocumentsByType(new String[]{"DOC", "DOCX"}));
        chipExcel.setOnClickListener(v -> filterDocumentsByType(new String[]{"XLS", "XLSX"}));
    }

    private void filterDocumentsByType(String[] types) {
        List<Document> filtered = new ArrayList<>();
        for (Document doc : fullDocumentList) {
            if (doc.getType() == null) continue;
            for (String t : types) {
                if (doc.getType().equalsIgnoreCase(t)) filtered.add(doc);
            }
        }
        documentAdapter.updateList(filtered);
    }

    private void loadDocumentData() {
        String ormawaId = sessionManager.getIdOrmawa();
        ApiService apiService = ApiClient.getApiService();

        apiService.getDocumentsByOrmawa(ormawaId).enqueue(new Callback<ApiResponseList<Document>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Document>> call, Response<ApiResponseList<Document>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fullDocumentList.clear();
                    fullDocumentList.addAll(response.body().getData());
                    displayDocumentList.clear();
                    displayDocumentList.addAll(fullDocumentList);
                    documentAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DocumentActivity.this, "Gagal memuat dokumen", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<Document>> call, Throwable t) {
                Toast.makeText(DocumentActivity.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

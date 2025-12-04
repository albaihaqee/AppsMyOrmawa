package com.inovarka.myormawa.views.dashboard.student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.FormListAdapter;
import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.models.FormInfo;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OprecActivity extends AppCompatActivity {

    private static final String TAG = "OprecActivity";

    private RecyclerView rvOprec;
    private FormListAdapter adapter;
    private ChipGroup chipGroup;
    private ProgressBar progressBar;
    private View layoutEmpty;
    private TextView txtEmpty;

    private List<FormInfo> allForms;
    private List<FormInfo> filteredForms;

    private String currentType = "anggota"; // anggota or event
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_oprec);

        initViews();
        setupRecyclerView();
        setupChips();
        loadForms();
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
        progressBar = findViewById(R.id.progress_bar);
        layoutEmpty = findViewById(R.id.layout_empty_state);
        txtEmpty = findViewById(R.id.txt_empty_state);

        apiService = ApiClient.getClient().create(ApiService.class);
        allForms = new ArrayList<>();
        filteredForms = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new FormListAdapter();
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
                    currentType = "anggota";
                } else {
                    currentType = "event";
                }

                loadForms();
            }
        });
    }

    private void loadForms() {
        showLoading(true);

        apiService.getFormsByType(currentType).enqueue(new Callback<ApiResponseList<FormInfo>>() {
            @Override
            public void onResponse(Call<ApiResponseList<FormInfo>> call,
                                   Response<ApiResponseList<FormInfo>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseList<FormInfo> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        allForms = apiResponse.getData();
                        filteredForms = new ArrayList<>(allForms);
                        adapter.setFormList(filteredForms);

                        updateEmptyState();
                    } else {
                        showError(apiResponse.getMessage());
                        updateEmptyState();
                    }
                } else {
                    showError("Gagal memuat data");
                    updateEmptyState();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<FormInfo>> call, Throwable t) {
                showLoading(false);
                showError("Koneksi gagal: " + t.getMessage());
                Log.e(TAG, "Error loading forms", t);
                updateEmptyState();
            }
        });
    }

    private void showOprecDetailDialog(FormInfo formInfo) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_oprec_detail, null);

        ImageView btnClose = view.findViewById(R.id.btn_close_dialog);
        TextView txtTitle = view.findViewById(R.id.txt_oprec_detail_title);
        TextView txtOrganization = view.findViewById(R.id.txt_oprec_detail_organization);
        TextView txtDeadline = view.findViewById(R.id.txt_oprec_detail_deadline);
        TextView txtParticipants = view.findViewById(R.id.txt_oprec_detail_participants);
        TextView txtDescription = view.findViewById(R.id.txt_oprec_detail_description);
        MaterialButton btnRegister = view.findViewById(R.id.btn_register_now);

        txtTitle.setText(formInfo.getJudul());
        txtOrganization.setText(formInfo.getNamaOrmawa());
        txtDeadline.setText("Dibuat: " + formInfo.getCreatedAt());
        txtParticipants.setText(formInfo.getParticipantsText());
        txtDescription.setText(formInfo.getDeskripsi());

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnRegister.setOnClickListener(v -> {
            dialog.dismiss();
            navigateToForm(formInfo);
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void navigateToForm(FormInfo formInfo) {
        Intent intent = new Intent(this, FormFillActivity.class);
        intent.putExtra(FormFillActivity.EXTRA_FORM_ID, formInfo.getId());
        intent.putExtra(FormFillActivity.EXTRA_FORM_TYPE, formInfo.getJenisForm());
        startActivity(intent);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvOprec.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateEmptyState() {
        if (filteredForms.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvOprec.setVisibility(View.GONE);

            if (currentType.equals("anggota")) {
                txtEmpty.setText("Belum ada pendaftaran anggota tersedia");
            } else {
                txtEmpty.setText("Belum ada pendaftaran event tersedia");
            }
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvOprec.setVisibility(View.VISIBLE);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
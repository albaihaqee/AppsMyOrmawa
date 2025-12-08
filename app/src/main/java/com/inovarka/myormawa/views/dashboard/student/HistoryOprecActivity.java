package com.inovarka.myormawa.views.dashboard.student;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.HistoryOprecAdapter;
import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.models.FormInfo;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.network.ApiService;
import com.inovarka.myormawa.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryOprecActivity extends AppCompatActivity {

    private static final String TAG = "HistoryOprecActivity";

    private ImageView btnBack;
    private ChipGroup chipGroupStatus;
    private Chip chipAll, chipPending, chipApproved, chipRejected;
    private RecyclerView rvHistoryOprec;
    private LinearLayout layoutEmpty, layoutLoading;

    private HistoryOprecAdapter adapter;
    private List<FormInfo> allHistoryList = new ArrayList<>();
    private List<FormInfo> filteredList = new ArrayList<>();

    private String userId;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_history_oprec);

        loadUserId();
        initViews();
        setupRecyclerView();
        setupChipListeners();
        setupListeners();
        loadHistoryData();
    }

    private void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));
    }

    private void loadUserId() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = prefs.getString(Constants.KEY_USER_ID, "0");
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        chipGroupStatus = findViewById(R.id.chip_group_status);
        chipAll = findViewById(R.id.chip_all);
        chipPending = findViewById(R.id.chip_pending);
        chipApproved = findViewById(R.id.chip_approved);
        chipRejected = findViewById(R.id.chip_rejected);
        rvHistoryOprec = findViewById(R.id.rv_history_oprec);
        layoutEmpty = findViewById(R.id.layout_empty);
        layoutLoading = findViewById(R.id.layout_loading);
    }

    private void setupRecyclerView() {
        adapter = new HistoryOprecAdapter(this, formInfo -> {
            Toast.makeText(this, "Detail: " + formInfo.getJudul(), Toast.LENGTH_SHORT).show();
        });

        rvHistoryOprec.setLayoutManager(new LinearLayoutManager(this));
        rvHistoryOprec.setAdapter(adapter);
    }

    private void setupChipListeners() {
        chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }

            int checkedId = checkedIds.get(0);

            if (checkedId == R.id.chip_all) {
                currentFilter = "all";
            } else if (checkedId == R.id.chip_pending) {
                currentFilter = "pending";
            } else if (checkedId == R.id.chip_approved) {
                currentFilter = "approved";
            } else if (checkedId == R.id.chip_rejected) {
                currentFilter = "rejected";
            }

            filterData(currentFilter);
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadHistoryData() {
        showLoading(true);

        ApiService apiService = new ApiClient().getClient().create(ApiService.class);
        Call<ApiResponseList<FormInfo>> call = apiService.getUserSubmissions(userId, "anggota");

        call.enqueue(new Callback<ApiResponseList<FormInfo>>() {
            @Override
            public void onResponse(Call<ApiResponseList<FormInfo>> call, Response<ApiResponseList<FormInfo>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseList<FormInfo> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        allHistoryList = apiResponse.getData();

                        if (allHistoryList != null && !allHistoryList.isEmpty()) {
                            filterData(currentFilter);
                        } else {
                            showEmpty(true);
                        }
                    } else {
                        showEmpty(true);
                        Toast.makeText(HistoryOprecActivity.this,
                                apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showEmpty(true);
                    Toast.makeText(HistoryOprecActivity.this,
                            "Gagal memuat data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<FormInfo>> call, Throwable t) {
                showLoading(false);
                showEmpty(true);
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(HistoryOprecActivity.this,
                        "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterData(String filter) {
        filteredList.clear();

        if (filter.equals("all")) {
            filteredList.addAll(allHistoryList);
        } else {
            for (FormInfo formInfo : allHistoryList) {
                if (formInfo.getStatus() != null &&
                        formInfo.getStatus().equalsIgnoreCase(filter)) {
                    filteredList.add(formInfo);
                }
            }
        }

        adapter.setHistoryList(filteredList);

        if (filteredList.isEmpty()) {
            showEmpty(true);
        } else {
            showEmpty(false);
        }
    }

    private void showLoading(boolean show) {
        layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        rvHistoryOprec.setVisibility(show ? View.GONE : View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void showEmpty(boolean show) {
        layoutEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        rvHistoryOprec.setVisibility(show ? View.GONE : View.VISIBLE);
        layoutLoading.setVisibility(View.GONE);
    }
}
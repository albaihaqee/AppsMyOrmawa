package com.inovarka.myormawa.views.dashboard.student;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.OrganizationAdapter;
import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.models.Organization;
import com.inovarka.myormawa.network.ApiClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizationFragment extends Fragment {

    private static final String TAG = "OrganizationFragment";

    private EditText etSearch;
    private TextView txtOrganizationCount;
    private ChipGroup chipGroupCategory;
    private RecyclerView rvOrganizations;
    private ProgressBar progressBar;

    private OrganizationAdapter adapter;
    private List<Organization> allOrganizations = new ArrayList<>();
    private List<Organization> filteredOrganizations = new ArrayList<>();
    private String selectedCategory = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organization, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupStatusBar();
        initViews(view);
        setupRecyclerView();
        setupChipGroup();
        setupSearchListener();
        loadOrganizationsFromAPI();
    }

    private void setupStatusBar() {
        if (getActivity() != null && getActivity().getWindow() != null) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.primary_blue));
            new WindowInsetsControllerCompat(window, window.getDecorView()).setAppearanceLightStatusBars(false);
        }
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.et_search_organization);
        txtOrganizationCount = view.findViewById(R.id.txt_organization_count);
        chipGroupCategory = view.findViewById(R.id.chip_group_category);
        rvOrganizations = view.findViewById(R.id.rv_organizations);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupRecyclerView() {
        adapter = new OrganizationAdapter(requireContext(), new ArrayList<>(), organization -> {
            OrganizationDetailDialogFragment.newInstance(organization).show(getParentFragmentManager(), "detail");
        });
        rvOrganizations.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrganizations.setAdapter(adapter);
    }

    private void setupChipGroup() {
        chipGroupCategory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = getView().findViewById(checkedIds.get(0));
                if (chip != null) {
                    String chipText = chip.getText().toString();
                    selectedCategory = chipText.equals("Semua") ? "All" : chipText;
                    filterOrganizations();
                }
            }
        });
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrganizations();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadOrganizationsFromAPI() {
        showLoading(true);

        ApiClient.getApiService().getAllOrganizations().enqueue(new Callback<ApiResponseList<Organization>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Organization>> call, Response<ApiResponseList<Organization>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allOrganizations = response.body().getData();
                    filterOrganizations();
                    Log.d(TAG, "Loaded " + allOrganizations.size() + " organizations");
                } else {
                    showError("Gagal memuat data organisasi");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<Organization>> call, Throwable t) {
                showLoading(false);
                showError("Gagal terhubung: " + t.getMessage());
                Log.e(TAG, "API Error", t);
            }
        });
    }

    private void filterOrganizations() {
        String query = etSearch.getText().toString().toLowerCase().trim();
        filteredOrganizations.clear();

        for (Organization org : allOrganizations) {
            boolean matchesCategory = selectedCategory.equals("All") || org.getCategory().equals(selectedCategory);
            boolean matchesSearch = query.isEmpty() || org.getName().toLowerCase().contains(query)
                    || org.getDescription().toLowerCase().contains(query);

            if (matchesCategory && matchesSearch) {
                filteredOrganizations.add(org);
            }
        }

        Collections.sort(filteredOrganizations, Comparator.comparing(Organization::getName));
        adapter.updateOrganizations(filteredOrganizations);
        txtOrganizationCount.setText(filteredOrganizations.size() + " Organisasi");
    }

    private void showLoading(boolean show) {
        if (progressBar != null) progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (rvOrganizations != null) rvOrganizations.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        if (getContext() != null) Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
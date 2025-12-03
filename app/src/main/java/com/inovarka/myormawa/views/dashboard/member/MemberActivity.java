package com.inovarka.myormawa.views.dashboard.member;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.MemberAdapter;
import com.inovarka.myormawa.models.Member;
import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.network.ApiService;
import com.inovarka.myormawa.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberActivity extends AppCompatActivity {

    private RecyclerView rvMembers;
    private MemberAdapter memberAdapter;
    private List<Member> memberList;
    private TextView tvMemberCount;
    private LinearLayout layoutEmptyState;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        // Initialize views
        ImageView btnBack = findViewById(R.id.btn_back);
        TextView tvTitle = findViewById(R.id.tv_title);
        rvMembers = findViewById(R.id.rv_members);
        tvMemberCount = findViewById(R.id.tv_member_count);
        layoutEmptyState = findViewById(R.id.layout_empty_state);

        sessionManager = new SessionManager(this);

        tvTitle.setText("Daftar Anggota");

        btnBack.setOnClickListener(v -> finish());

        setupRecyclerView();
        loadMemberDataFromApi();
    }

    private void setupRecyclerView() {
        memberList = new ArrayList<>();
        memberAdapter = new MemberAdapter(memberList);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        rvMembers.setAdapter(memberAdapter);
    }

    private void loadMemberDataFromApi() {
        String idOrmawa = sessionManager.getIdOrmawa();
        if (idOrmawa == null || idOrmawa.isEmpty()) {
            Toast.makeText(this, "Ormawa tidak ditemukan!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponseList<Member>> call = apiService.getMembersByOrmawa(idOrmawa);

        call.enqueue(new Callback<ApiResponseList<Member>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Member>> call, Response<ApiResponseList<Member>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseList<Member> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        memberList.clear();
                        memberList.addAll(apiResponse.getData());
                        memberAdapter.notifyDataSetChanged();

                        tvMemberCount.setText("Total: " + memberList.size() + " Anggota");
                        toggleEmptyState(memberList.isEmpty());
                    } else {
                        Toast.makeText(MemberActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        toggleEmptyState(true);
                    }
                } else {
                    Toast.makeText(MemberActivity.this, "Gagal memuat data anggota!", Toast.LENGTH_SHORT).show();
                    toggleEmptyState(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<Member>> call, Throwable t) {
                Toast.makeText(MemberActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                toggleEmptyState(true);
            }
        });

    }

    private void toggleEmptyState(boolean isEmpty) {
        if (isEmpty) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvMembers.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvMembers.setVisibility(View.VISIBLE);
        }
    }
}

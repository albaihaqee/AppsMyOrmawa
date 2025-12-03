package com.inovarka.myormawa.views.dashboard.member;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.MemberAdapter;
import com.inovarka.myormawa.models.Member;

import java.util.ArrayList;
import java.util.List;

public class MemberActivity extends AppCompatActivity {

    private RecyclerView rvMembers;
    private MemberAdapter memberAdapter;
    private List<Member> memberList;
    private TextView tvMemberCount;
    private LinearLayout layoutEmptyState;


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


        // Update title
        tvTitle.setText("Daftar Anggota");

        btnBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        setupRecyclerView();

        // Load data
        loadMemberData();
    }

    private void setupRecyclerView() {
        memberList = new ArrayList<>();
        memberAdapter = new MemberAdapter(memberList);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        rvMembers.setAdapter(memberAdapter);
    }

    private void loadMemberData() {
        // Data dummy - ganti dengan data dari API
        memberList.add(new Member("1", "Ahmad Budi Santoso", "IT Department", "Manager", "081234567890", "Teknik Informatika"));
        memberList.add(new Member("2", "Siti Nurhaliza", "Finance", "Staff", "081234567891", "Akuntansi"));
        memberList.add(new Member("3", "Budi Setiawan", "Marketing", "Supervisor", "081234567892", "Manajemen"));
        memberList.add(new Member("4", "Dewi Kusuma", "HR", "Staff", "081234567893", "Psikologi"));

        memberAdapter.notifyDataSetChanged();

        tvMemberCount.setText("Total: " + memberList.size() + " Anggota");

        // 🔥 Tampilkan empty state bila list kosong
        if (memberList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvMembers.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvMembers.setVisibility(View.VISIBLE);
        }
    }
}
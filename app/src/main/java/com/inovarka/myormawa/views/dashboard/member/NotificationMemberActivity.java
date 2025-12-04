package com.inovarka.myormawa.views.dashboard.member;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.NotificationMemberAdapter;
import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.models.Meeting;
import com.inovarka.myormawa.models.NotificationMember;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.network.ApiService;
import com.inovarka.myormawa.utils.SessionManager;
import com.inovarka.myormawa.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationMemberActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationMemberAdapter adapter;
    private ImageView btnBack;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_member);

        recyclerView = findViewById(R.id.rv_notifications);
        btnBack = findViewById(R.id.btn_back);

        // ⬅️ Tambahkan ini
        prefs = getSharedPreferences("notif_prefs", MODE_PRIVATE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationMemberAdapter();
        recyclerView.setAdapter(adapter);

        loadNotifications();

        btnBack.setOnClickListener(v -> onBackPressed());
    }


    private void loadNotifications() {
        SessionManager sm = new SessionManager(this);
        String ormawaId = sm.getIdOrmawa();

        ApiService api = ApiClient.getApiService();
        api.getKegiatanByOrmawa(ormawaId)
                .enqueue(new Callback<ApiResponseList<Meeting>>() {
                    @Override
                    public void onResponse(Call<ApiResponseList<Meeting>> call, Response<ApiResponseList<Meeting>> resp) {
                        if (!resp.isSuccessful() || resp.body() == null) return;

                        List<Meeting> list = resp.body().getData();

                        prefs.edit().putInt("last_seen_count", list.size()).apply();


                        List<NotificationMember> notifList = new ArrayList<>();

                        for (Meeting m : list) {
                            notifList.add(new NotificationMember(
                                    m.getId(),
                                    m.getNama(),
                                    m.getAgenda(),
                                    m.getTanggal(),
                                    m.getJamMulai() + " - " + m.getJamSelesai(),
                                    m.getLokasi(),
                                    TimeUtils.getTimeAgo(m.getCreatedAt())
                            ));
                        }


                        adapter.setItems(notifList);
                    }

                    @Override
                    public void onFailure(Call<ApiResponseList<Meeting>> call, Throwable t) {}
                });
    }
}

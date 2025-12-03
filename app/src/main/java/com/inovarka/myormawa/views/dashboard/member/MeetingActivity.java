package com.inovarka.myormawa.views.dashboard.member;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.MeetingAdapter;
import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.models.Meeting;
import com.inovarka.myormawa.models.ReminderItem;
import com.inovarka.myormawa.models.ReminderReceiver;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.network.ApiService;
import com.inovarka.myormawa.utils.SessionManager;
import com.inovarka.myormawa.utils.ReminderStorage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingActivity extends AppCompatActivity {

    private RecyclerView rvKegiatan;
    private MeetingAdapter adapter;
    private List<Meeting> fullList = new ArrayList<>();
    private List<Meeting> displayList = new ArrayList<>();
    private SessionManager sessionManager;

    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;
    private TextView txtEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        sessionManager = new SessionManager(this);

        rvKegiatan = findViewById(R.id.rv_meetings);
        ImageView btnBack = findViewById(R.id.btn_back);
        TextView tvTitle = findViewById(R.id.tv_title);
        progressBar = findViewById(R.id.progress_bar);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        txtEmptyMessage = findViewById(R.id.txt_empty_message);

        tvTitle.setText("Riwayat Kegiatan");
        btnBack.setOnClickListener(v -> finish());

        adapter = new MeetingAdapter(this, displayList);
        rvKegiatan.setLayoutManager(new LinearLayoutManager(this));
        rvKegiatan.setAdapter(adapter);

        adapter.setOnReminderClick((kegiatan, bell) -> showReminderOptions(kegiatan));

        loadKegiatan();
    }

    private void showReminderOptions(Meeting kegiatan) {
        String[] options = {"10 menit sebelum", "30 menit sebelum", "1 jam sebelum", "Hapus Reminder", "Batal"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Reminder");
        builder.setItems(options, (dialog, which) -> {
            ReminderItem item = null;

            if (which == 0) item = new ReminderItem(kegiatan, 10);
            else if (which == 1) item = new ReminderItem(kegiatan, 30);
            else if (which == 2) item = new ReminderItem(kegiatan, 60);

            if (item != null) {
                ReminderStorage.addReminder(this, item); // simpan ke SharedPreferences
                ReminderStorage.scheduleReminder(this, item); // schedule AlarmManager
            }

            if (which == 3) {
                ReminderStorage.removeReminder(this, kegiatan.getId());
            }

            adapter.notifyDataSetChanged();
        });
        builder.show();
    }

    public static void scheduleReminder(Context context, ReminderItem item) {
        long meetingTime = item.getMeeting().getTimeInMillis(); // convert tanggal+waktu ke millis
        long reminderTime = meetingTime - item.getMinutesBefore() * 60 * 1000;

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("meeting_name", item.getMeeting().getNama());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                item.getMeeting().getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        }
    }

    private void loadKegiatan() {
        showLoading(true);
        ApiService api = ApiClient.getApiService();
        String ormawaId = sessionManager.getIdOrmawa();

        api.getKegiatanByOrmawa(ormawaId).enqueue(new Callback<ApiResponseList<Meeting>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Meeting>> call, Response<ApiResponseList<Meeting>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    fullList.clear();
                    fullList.addAll(response.body().getData());
                    displayList.clear();
                    displayList.addAll(fullList);
                    adapter.notifyDataSetChanged();

                    toggleEmptyState(displayList.isEmpty());
                } else {
                    toggleEmptyState(true);
                    txtEmptyMessage.setText("Belum ada riwayat kegiatan");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<Meeting>> call, Throwable t) {
                showLoading(false);
                toggleEmptyState(true);
                txtEmptyMessage.setText("Gagal memuat kegiatan: " + t.getMessage());
            }
        });
    }

    private void toggleEmptyState(boolean isEmpty) {
        if (isEmpty) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvKegiatan.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvKegiatan.setVisibility(View.VISIBLE);
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) {
            rvKegiatan.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }
}

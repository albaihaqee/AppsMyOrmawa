package com.inovarka.myormawa.views.dashboard.member;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.EventMemberAdapter;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.network.ApiService;
import com.inovarka.myormawa.component.EventDetailDialog;
import com.inovarka.myormawa.models.EventMember;
import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventFragment extends Fragment {

    private RecyclerView rvEvents;
    private EventMemberAdapter eventAdapter;
    private List<EventMember> eventList = new ArrayList<>();
    private LinearLayout layoutEmptyState;
    private ProgressBar progressBar;
    private TextView tvEventCount;

    public EventFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupStatusBar();
        initViews(view);
        setupRecyclerView();

        loadEventData();
    }

    private void initViews(View view) {
        rvEvents = view.findViewById(R.id.rv_events);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEventCount = view.findViewById(R.id.tv_event_count);
    }

    private void setupStatusBar() {
        if (getActivity() != null && getActivity().getWindow() != null) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.primary_blue));

            WindowInsetsControllerCompat wic =
                    new WindowInsetsControllerCompat(window, window.getDecorView());
            wic.setAppearanceLightStatusBars(false);
        }
    }

    private void setupRecyclerView() {
        eventAdapter = new EventMemberAdapter(eventList, this::showEventDetailDialog);
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvEvents.setAdapter(eventAdapter);
    }

    private void loadEventData() {
        showLoading(true);

        SessionManager sm = new SessionManager(requireContext());
        String ormawaId = sm.getIdOrmawa();

        ApiService api = ApiClient.getApiService();
        api.getEventsByOrmawa(ormawaId).enqueue(new Callback<ApiResponseList<EventMember>>() {
            @Override
            public void onResponse(Call<ApiResponseList<EventMember>> call,
                                   Response<ApiResponseList<EventMember>> response) {

                showLoading(false);

                if (!response.isSuccessful() || response.body() == null) {
                    showEmptyState();
                    Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiResponseList<EventMember> res = response.body();

                if (res.isSuccess()) {
                    eventList.clear();
                    eventList.addAll(res.getData());
                    eventAdapter.notifyDataSetChanged();

                    updateEventCount();
                    updateEmptyState();
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseList<EventMember>> call, Throwable t) {
                showLoading(false);
                showEmptyState();
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvEvents.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateEventCount() {
        tvEventCount.setText("Menampilkan " + eventList.size() + " Event");
    }

    private void updateEmptyState() {
        layoutEmptyState.setVisibility(eventList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showEmptyState() {
        rvEvents.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
    }

    private void showEventDetailDialog(EventMember event) {
        EventDetailDialog dialog = new EventDetailDialog(getContext(), event);
        dialog.show();
    }
}

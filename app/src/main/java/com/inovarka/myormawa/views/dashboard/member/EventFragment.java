package com.inovarka.myormawa.views.dashboard.member;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.EventMemberAdapter;
import com.inovarka.myormawa.component.EventDetailDialog;
import com.inovarka.myormawa.models.EventMember;

import java.util.ArrayList;
import java.util.List;

public class EventFragment extends Fragment {

    private RecyclerView rvEvents;
    private EventMemberAdapter eventAdapter;
    private List<EventMember> eventList;
    private List<EventMember> eventListFull;
    private LinearLayout layoutEmptyState;
    private ProgressBar progressBar;
    private TextView tvEventCount;
    private String currentFilter = "all";

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

            WindowInsetsControllerCompat windowInsetsController = new WindowInsetsControllerCompat(window, window.getDecorView());
            windowInsetsController.setAppearanceLightStatusBars(false);
        }
    }
    private void setupRecyclerView() {
        eventList = new ArrayList<>();
        eventListFull = new ArrayList<>();
        eventAdapter = new EventMemberAdapter(eventList, event -> {
            // Handle event click - show dialog
            showEventDetailDialog(event);
        });
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvEvents.setAdapter(eventAdapter);
    }





    private void filterEvents() {
        eventList.clear();

        if (currentFilter.equals("all")) {
            eventList.addAll(eventListFull);
        } else {
            for (EventMember event : eventListFull) {
                if (event.getStatus().equals(currentFilter)) {
                    eventList.add(event);
                }
            }
        }

        eventAdapter.notifyDataSetChanged();
        updateEventCount();
        updateEmptyState();
    }

    private void loadEventData() {
        showLoading(true);

        // Simulate loading
        if (getView() != null) {
            getView().postDelayed(() -> {
                // Data dummy - ganti dengan data dari API
                eventListFull.add(new EventMember("1", "Workshop UI/UX Design for Beginner",
                        "Workshop", "Lab Multimedia Gedung JTI", "20 Nov 2025", "09:00", "17:00",
                        120, "", "upcoming", "Belajar desain UI/UX dari nol"));

                eventListFull.add(new EventMember("2", "Seminar Teknologi AI",
                        "Seminar", "Auditorium Utama", "22 Nov 2025", "10:00", "15:00",
                        200, "", "upcoming", "Pengenalan AI dan implementasinya"));

                eventListFull.add(new EventMember("3", "Kompetisi Mobile Apps Development",
                        "Kompetisi", "Online", "25 Nov 2025", "08:00", "18:00",
                        85, "", "ongoing", "Lomba membuat aplikasi mobile"));

                eventListFull.add(new EventMember("4", "Pelatihan Public Speaking",
                        "Pelatihan", "Ruang Seminar Lt. 2", "18 Nov 2025", "13:00", "16:00",
                        50, "", "finished", "Meningkatkan skill public speaking"));

                eventListFull.add(new EventMember("5", "Hackathon Innovation Week",
                        "Kompetisi", "Gedung Innovation Center", "28 Nov 2025", "08:00", "20:00",
                        150, "", "upcoming", "Hackathon untuk solusi inovatif"));

                filterEvents();
                showLoading(false);
            }, 1000);
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvEvents.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateEventCount() {
        tvEventCount.setText("Menampilkan " + eventList.size() + " Event");
    }

    private void updateEmptyState() {
        if (eventList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvEvents.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvEvents.setVisibility(View.VISIBLE);
        }
    }

    private void showEventDetailDialog(EventMember event) {
        EventDetailDialog dialog = new EventDetailDialog(getContext(), event);
        dialog.show();
    }
}
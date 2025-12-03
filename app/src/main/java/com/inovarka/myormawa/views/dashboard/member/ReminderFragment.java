package com.inovarka.myormawa.views.dashboard.member;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.ReminderAdapter;
import com.inovarka.myormawa.models.ReminderItem;
import com.inovarka.myormawa.utils.ReminderStorage;

import java.util.List;

public class ReminderFragment extends Fragment {

    private RecyclerView rvReminder;
    private ReminderAdapter adapter;
    private View rootView;

    public ReminderFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        rootView = inflater.inflate(R.layout.fragment_reminder, container, false);

        rvReminder = rootView.findViewById(R.id.rv_reminder);
        rvReminder.setLayoutManager(new LinearLayoutManager(getContext()));

        setupStatusBar();
        loadReminders();

        return rootView;
    }

    private void setupStatusBar() {
        if (getActivity() != null && getActivity().getWindow() != null) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.primary_blue));

            WindowInsetsControllerCompat windowInsetsController =
                    new WindowInsetsControllerCompat(window, window.getDecorView());
            windowInsetsController.setAppearanceLightStatusBars(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadReminders(); // refresh data saat kembali
    }

    private void loadReminders() {

        if (rootView == null) return; // safety

        List<ReminderItem> list = ReminderStorage.getReminders(getContext());

        adapter = new ReminderAdapter(list);
        rvReminder.setAdapter(adapter);

        // Empty state handling
        View emptyState = rootView.findViewById(R.id.tv_empty_state);

        if (list == null || list.isEmpty()) {
            rvReminder.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvReminder.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }
}

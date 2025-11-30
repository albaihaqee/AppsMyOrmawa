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

    public ReminderFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_reminder, container, false);
        rvReminder = v.findViewById(R.id.rv_reminder);
        rvReminder.setLayoutManager(new LinearLayoutManager(getContext()));

        setupStatusBar();
        loadReminders();

        return v;
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

    @Override
    public void onResume() {
        super.onResume();
        loadReminders();
    }

    private void loadReminders() {
        List<ReminderItem> list = ReminderStorage.getReminders(getContext());
        adapter = new ReminderAdapter(list);
        rvReminder.setAdapter(adapter);
    }
}

package com.inovarka.myormawa.views.dashboard.student;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.CalendarDateAdapter;
import com.inovarka.myormawa.adapters.CalendarEventAdapter;
import com.inovarka.myormawa.models.CalendarDate;
import com.inovarka.myormawa.models.CalendarEvent;
import com.inovarka.myormawa.repositories.CalendarRepository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarFragment extends Fragment {

    private static final String TAG = "CalendarFragment";

    private TextView txtMonthYear;
    private TextView txtSelectedDate;
    private ImageView btnPrevMonth;
    private ImageView btnNextMonth;
    private RecyclerView rvCalendarDates;
    private RecyclerView rvEvents;
    private LinearLayout layoutEmptyEvent;
    private LinearLayout loadingView;

    private Calendar currentCalendar;
    private Calendar selectedDate;
    private Map<String, List<CalendarEvent>> eventsMap;
    private List<CalendarEvent> allEvents;

    private CalendarDateAdapter dateAdapter;
    private CalendarEventAdapter eventAdapter;
    private CalendarRepository calendarRepository;

    private SimpleDateFormat monthYearFormat;
    private SimpleDateFormat selectedDateFormat;
    private SimpleDateFormat dateKeyFormat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        monthYearFormat = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));
        selectedDateFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("id", "ID"));
        dateKeyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        currentCalendar = Calendar.getInstance();
        selectedDate = (Calendar) currentCalendar.clone();

        calendarRepository = new CalendarRepository();
        allEvents = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupStatusBar();
        initViews(view);
        setupRecyclerViews();
        setupListeners();

        // Load data dari API
        loadCalendarEvents();
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

    private void initViews(View view) {
        txtMonthYear = view.findViewById(R.id.txt_month_year);
        txtSelectedDate = view.findViewById(R.id.txt_selected_date);
        btnPrevMonth = view.findViewById(R.id.btn_prev_month);
        btnNextMonth = view.findViewById(R.id.btn_next_month);
        rvCalendarDates = view.findViewById(R.id.rv_calendar_dates);
        rvEvents = view.findViewById(R.id.rv_events);
        layoutEmptyEvent = view.findViewById(R.id.layout_empty_event);
        loadingView = view.findViewById(R.id.loading_view);
    }

    private void setupRecyclerViews() {
        dateAdapter = new CalendarDateAdapter(new ArrayList<>(), date -> {
            selectedDate = (Calendar) date.clone();
            updateSelectedDateText();
            updateEventsForSelectedDate();
            dateAdapter.setSelectedDate(selectedDate);
        });
        rvCalendarDates.setLayoutManager(new GridLayoutManager(getContext(), 7));
        rvCalendarDates.setAdapter(dateAdapter);

        eventAdapter = new CalendarEventAdapter(new ArrayList<>());
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvEvents.setAdapter(eventAdapter);
    }

    private void setupListeners() {
        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateUI();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateUI();
        });
    }

    /**
     * === PERUBAHAN UTAMA: LOAD DATA DARI API ===
     */
    private void loadCalendarEvents() {
        showLoading();

        calendarRepository.getCalendarEvents().observe(getViewLifecycleOwner(), new Observer<List<CalendarEvent>>() {
            @Override
            public void onChanged(List<CalendarEvent> events) {
                hideLoading();

                if (events != null && !events.isEmpty()) {
                    Log.d(TAG, "Calendar events loaded: " + events.size());
                    allEvents = events;

                    // Convert list ke Map (dateKey -> List<Event>)
                    eventsMap = calendarRepository.getEventsByDate(events);

                    Log.d(TAG, "Events map created with " + eventsMap.size() + " dates");

                    // Update UI
                    updateUI();
                } else {
                    Log.d(TAG, "No calendar events found");
                    allEvents = new ArrayList<>();
                    eventsMap = null;
                    updateUI();
                }
            }
        });
    }

    private void updateUI() {
        txtMonthYear.setText(monthYearFormat.format(currentCalendar.getTime()));
        updateCalendarDates();
        updateSelectedDateText();
        updateEventsForSelectedDate();
    }

    private void updateCalendarDates() {
        List<CalendarDate> dates = new ArrayList<>();
        Calendar calendar = (Calendar) currentCalendar.clone();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        calendar.add(Calendar.DAY_OF_MONTH, -(firstDayOfWeek - 1));

        for (int i = 0; i < 42; i++) {
            boolean isCurrentMonth = calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH);
            CalendarDate calendarDate = new CalendarDate((Calendar) calendar.clone(), isCurrentMonth);

            Calendar today = Calendar.getInstance();
            if (isSameDay(calendar, today)) {
                calendarDate.setToday(true);
            }

            // === CEK APAKAH TANGGAL INI PUNYA EVENT ===
            String dateKey = dateKeyFormat.format(calendar.getTime());
            if (eventsMap != null && eventsMap.containsKey(dateKey)) {
                calendarDate.setHasEvent(true);
                Log.d(TAG, "Date " + dateKey + " has events: " + eventsMap.get(dateKey).size());
            }

            dates.add(calendarDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        dateAdapter.updateDates(dates);
        dateAdapter.setSelectedDate(selectedDate);
    }

    private void updateSelectedDateText() {
        String dateText = selectedDateFormat.format(selectedDate.getTime());

        Calendar today = Calendar.getInstance();
        if (isSameDay(selectedDate, today)) {
            dateText += " (Hari ini)";
        }

        txtSelectedDate.setText(dateText);
    }

    private void updateEventsForSelectedDate() {
        String dateKey = dateKeyFormat.format(selectedDate.getTime());

        Log.d(TAG, "Selected date key: " + dateKey);

        List<CalendarEvent> events = null;
        if (eventsMap != null) {
            events = eventsMap.get(dateKey);
        }

        if (events != null && !events.isEmpty()) {
            Log.d(TAG, "Showing " + events.size() + " events for " + dateKey);
            layoutEmptyEvent.setVisibility(View.GONE);
            rvEvents.setVisibility(View.VISIBLE);
            eventAdapter.updateEvents(events);
        } else {
            Log.d(TAG, "No events for " + dateKey);
            layoutEmptyEvent.setVisibility(View.VISIBLE);
            rvEvents.setVisibility(View.GONE);
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void showLoading() {
        if (loadingView != null) loadingView.setVisibility(View.VISIBLE);
        if (rvCalendarDates != null) rvCalendarDates.setVisibility(View.GONE);
        if (layoutEmptyEvent != null) layoutEmptyEvent.setVisibility(View.GONE);
        if (rvEvents != null) rvEvents.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (loadingView != null) loadingView.setVisibility(View.GONE);
        if (rvCalendarDates != null) rvCalendarDates.setVisibility(View.VISIBLE);
    }
}
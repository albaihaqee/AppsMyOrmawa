package com.inovarka.myormawa.views.dashboard.student;

import android.os.Bundle;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.CalendarDateAdapter;
import com.inovarka.myormawa.adapters.CalendarEventAdapter;
import com.inovarka.myormawa.models.CalendarDate;
import com.inovarka.myormawa.models.CalendarEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarFragment extends Fragment {

    private TextView txtMonthYear;
    private TextView txtSelectedDate;
    private ImageView btnPrevMonth;
    private ImageView btnNextMonth;
    private RecyclerView rvCalendarDates;
    private RecyclerView rvEvents;
    private LinearLayout layoutEmptyEvent;

    private Calendar currentCalendar;
    private Calendar selectedDate;
    private Map<String, List<CalendarEvent>> eventsMap;

    private CalendarDateAdapter dateAdapter;
    private CalendarEventAdapter eventAdapter;

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

        initializeDummyEvents();
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
        updateUI();
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
    }

    private void setupRecyclerViews() {
        dateAdapter = new CalendarDateAdapter(new ArrayList<>(), date -> {
            selectedDate = (Calendar) date.clone();
            updateUI();
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

            String dateKey = dateKeyFormat.format(calendar.getTime());
            if (eventsMap.containsKey(dateKey)) {
                calendarDate.setHasEvent(true);
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
        List<CalendarEvent> events = eventsMap.get(dateKey);

        if (events != null && !events.isEmpty()) {
            layoutEmptyEvent.setVisibility(View.GONE);
            rvEvents.setVisibility(View.VISIBLE);
            eventAdapter.updateEvents(events);
        } else {
            layoutEmptyEvent.setVisibility(View.VISIBLE);
            rvEvents.setVisibility(View.GONE);
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void initializeDummyEvents() {
        eventsMap = new HashMap<>();

        String[] colors = {"#5800FF", "#2C4EEF", "#00D7FF"};

        addEvent("2025-11-09", new CalendarEvent(
                "1",
                "Diesnatalis Teknik Informatika",
                "Aula Soetomo Wadojo",
                "07:00 am",
                createDate(2025, Calendar.NOVEMBER, 9),
                colors[0]
        ));
        addEvent("2025-11-09", new CalendarEvent(
                "2",
                "Workshop UI/UX Design",
                "Lab Multimedia",
                "13:00 pm",
                createDate(2025, Calendar.NOVEMBER, 9),
                colors[1]
        ));

        addEvent("2025-11-22", new CalendarEvent(
                "3",
                "Diesnatalis Teknik Informatika",
                "Aula Soetomo Wadojo",
                "07:00 am",
                createDate(2025, Calendar.NOVEMBER, 22),
                colors[0]
        ));
        addEvent("2025-11-22", new CalendarEvent(
                "4",
                "Workshop Laravel untuk Pemula",
                "Lab. Rekayasa Sistem Informasi",
                "13:00 pm",
                createDate(2025, Calendar.NOVEMBER, 22),
                colors[1]
        ));
        addEvent("2025-11-22", new CalendarEvent(
                "5",
                "Pelatihan Public Speaking",
                "Lantai 4.1 Gedung JTI",
                "14:00 pm",
                createDate(2025, Calendar.NOVEMBER, 22),
                colors[2]
        ));
        addEvent("2025-11-22", new CalendarEvent(
                "6",
                "Konser Art of Marunggalan 11.0",
                "Lapangan Hijau A3 POLIJE",
                "16:00 pm",
                createDate(2025, Calendar.NOVEMBER, 22),
                colors[0]
        ));

        addEvent("2025-11-24", new CalendarEvent(
                "7",
                "Seminar Nasional Teknologi",
                "Gedung Serba Guna",
                "09:00 am",
                createDate(2025, Calendar.NOVEMBER, 24),
                colors[0]
        ));
    }

    private void addEvent(String dateKey, CalendarEvent event) {
        if (!eventsMap.containsKey(dateKey)) {
            eventsMap.put(dateKey, new ArrayList<>());
        }
        eventsMap.get(dateKey).add(event);
    }

    private java.util.Date createDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
package com.inovarka.myormawa.views.dashboard.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.EventAdapter;
import com.inovarka.myormawa.models.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventActivity extends AppCompatActivity {

    private RecyclerView rvEvents;
    private EventAdapter adapter;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_event);

        initViews();
        setupRecyclerView();
        loadDummyData();
    }

    private void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        rvEvents = findViewById(R.id.rv_events);
    }

    private void setupRecyclerView() {
        adapter = new EventAdapter();
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(adapter);

        adapter.setOnItemClickListener(this::showEventDetailDialog);
    }

    private void showEventDetailDialog(Event event) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_event_detail, null);

        ImageView btnClose = view.findViewById(R.id.btn_close_dialog);
        TextView txtTitle = view.findViewById(R.id.txt_event_detail_title);
        TextView txtOrganizer = view.findViewById(R.id.txt_event_detail_organizer);
        TextView txtLocation = view.findViewById(R.id.txt_event_detail_location);
        TextView txtDate = view.findViewById(R.id.txt_event_detail_date);
        TextView txtTime = view.findViewById(R.id.txt_event_detail_time);
        TextView txtParticipants = view.findViewById(R.id.txt_event_detail_participants);
        TextView txtDescription = view.findViewById(R.id.txt_event_detail_description);
        View btnDownload = view.findViewById(R.id.btn_download_guidebook);

        txtTitle.setText(event.getTitle());
        txtOrganizer.setText(event.getOrganizer());
        txtLocation.setText(event.getLocation());
        txtDate.setText(event.getDate());
        txtTime.setText(event.getTime() != null && !event.getTime().isEmpty() ? event.getTime() : "-");
        txtParticipants.setText(event.getParticipantsText());
        txtDescription.setText(event.getDescription());

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnDownload.setOnClickListener(v -> {
            Toast.makeText(this, "Mengunduh buku panduan...", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void loadDummyData() {
        List<Event> allEvents = new ArrayList<>();

        allEvents.add(new Event(
                "1",
                "Diesnatalis Politeknik Negeri Jember",
                "Politeknik Negeri Jember",
                "15 Des 2025",
                "07:00 - 16:00",
                450,
                "https://images.unsplash.com/photo-1562774053-701939374585?w=400&h=533&fit=crop",
                "Aula Soetomo Wadojo",
                "Perayaan Dies Natalis Politeknik Negeri Jember yang ke-48 dengan berbagai rangkaian acara menarik.",
                "",
                "Perayaan"
        ));

        allEvents.add(new Event(
                "2",
                "Workshop UI/UX Design for Beginner",
                "Lab Multimedia Gedung JTI",
                "20 Nov 2025",
                "13:00 - 15:00",
                120,
                "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400&h=533&fit=crop",
                "Lab Multimedia Gedung JTI",
                "Workshop UI/UX Design untuk pemula yang ingin belajar desain antarmuka dan pengalaman pengguna.",
                "",
                "Workshop"
        ));

        allEvents.add(new Event(
                "3",
                "Seminar Nasional Teknologi Informasi",
                "Politeknik Negeri Jember",
                "25 Nov 2025",
                "08:00 - 12:00",
                200,
                "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=400&h=533&fit=crop",
                "Auditorium Gedung Utama",
                "Seminar nasional membahas perkembangan teknologi informasi terkini.",
                "",
                "Seminar"
        ));

        allEvents.add(new Event(
                "4",
                "Kompetisi UI/UX Design",
                "HMJ Teknologi Informasi",
                "1 Des 2025",
                "",
                150,
                "https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=400&h=533&fit=crop",
                "Online",
                "Kompetisi desain UI/UX untuk mahasiswa se-Indonesia.",
                "",
                "Kompetisi"
        ));

        allEvents.add(new Event(
                "5",
                "Pelatihan Web Development",
                "UKM Programming",
                "5 Des 2025",
                "09:00 - 16:00",
                80,
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400&h=533&fit=crop",
                "Lab Komputer JTI",
                "Pelatihan intensif web development dari basic hingga advanced.",
                "",
                "Workshop"
        ));

        // Filter: Only show events with date >= today
        eventList = filterUpcomingEvents(allEvents);
        adapter.setEventList(eventList);
    }

    private List<Event> filterUpcomingEvents(List<Event> events) {
        List<Event> upcomingEvents = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
        Date today = new Date();

        for (Event event : events) {
            try {
                Date eventDate = sdf.parse(event.getDate());
                if (eventDate != null && !eventDate.before(today)) {
                    upcomingEvents.add(event);
                }
            } catch (ParseException e) {
                // If parse fails, include the event anyway
                upcomingEvents.add(event);
            }
        }

        return upcomingEvents;
    }
}
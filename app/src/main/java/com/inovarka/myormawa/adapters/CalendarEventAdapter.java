package com.inovarka.myormawa.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.CalendarEvent;
import java.util.List;

public class CalendarEventAdapter extends RecyclerView.Adapter<CalendarEventAdapter.EventViewHolder> {

    private List<CalendarEvent> events;

    public CalendarEventAdapter(List<CalendarEvent> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_calendar, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        CalendarEvent event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateEvents(List<CalendarEvent> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        private final View colorBar;
        private final TextView txtTime;
        private final TextView txtTitle;
        private final TextView txtLocation;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            colorBar = itemView.findViewById(R.id.view_event_color);
            txtTime = itemView.findViewById(R.id.txt_event_time);
            txtTitle = itemView.findViewById(R.id.txt_event_title);
            txtLocation = itemView.findViewById(R.id.txt_event_location);
        }

        public void bind(CalendarEvent event) {
            txtTime.setText(event.getTime());
            txtTitle.setText(event.getTitle());
            txtLocation.setText(event.getLocation());

            try {
                colorBar.setBackgroundColor(Color.parseColor(event.getColor()));
            } catch (IllegalArgumentException e) {
                colorBar.setBackgroundColor(Color.parseColor("#2C4EEF"));
            }
        }
    }
}
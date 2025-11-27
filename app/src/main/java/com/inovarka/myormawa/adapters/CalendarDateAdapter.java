package com.inovarka.myormawa.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.CalendarDate;
import java.util.Calendar;
import java.util.List;

public class CalendarDateAdapter extends RecyclerView.Adapter<CalendarDateAdapter.DateViewHolder> {

    private List<CalendarDate> dates;
    private Calendar selectedDate;
    private OnDateClickListener listener;

    public interface OnDateClickListener {
        void onDateClick(Calendar date);
    }

    public CalendarDateAdapter(List<CalendarDate> dates, OnDateClickListener listener) {
        this.dates = dates;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        CalendarDate calendarDate = dates.get(position);
        holder.bind(calendarDate, selectedDate);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public void setSelectedDate(Calendar date) {
        this.selectedDate = date;
        notifyDataSetChanged();
    }

    public void updateDates(List<CalendarDate> newDates) {
        this.dates = newDates;
        notifyDataSetChanged();
    }

    class DateViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout dateBackground;
        private final TextView txtDateNumber;
        private final View eventIndicator;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateBackground = itemView.findViewById(R.id.date_background);
            txtDateNumber = itemView.findViewById(R.id.txt_date_number);
            eventIndicator = itemView.findViewById(R.id.event_indicator);
        }

        public void bind(CalendarDate calendarDate, Calendar selected) {
            int dayOfMonth = calendarDate.getDayOfMonth();
            txtDateNumber.setText(String.valueOf(dayOfMonth));

            dateBackground.setBackground(null);

            if (!calendarDate.isCurrentMonth()) {
                txtDateNumber.setTextColor(Color.parseColor("#CCCCCC"));
                eventIndicator.setVisibility(View.GONE);
            } else {
                boolean isSelected = selected != null && isSameDay(calendarDate.getDate(), selected);

                if (isSelected) {
                    dateBackground.setBackgroundResource(R.drawable.shape_calendar_selected);
                    txtDateNumber.setTextColor(Color.WHITE);
                    eventIndicator.setVisibility(View.GONE);
                } else {
                    txtDateNumber.setTextColor(Color.parseColor("#1A1A1A"));

                    if (calendarDate.hasEvent()) {
                        eventIndicator.setVisibility(View.VISIBLE);
                        eventIndicator.setBackgroundResource(R.drawable.shape_event_indicator);
                    } else {
                        eventIndicator.setVisibility(View.GONE);
                    }
                }
            }

            itemView.setOnClickListener(v -> {
                if (calendarDate.isCurrentMonth() && listener != null) {
                    listener.onDateClick(calendarDate.getDate());
                }
            });
        }

        private boolean isSameDay(Calendar cal1, Calendar cal2) {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        }
    }
}
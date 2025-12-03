package com.inovarka.myormawa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.AttendanceData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PresenceHistoryAdapter extends RecyclerView.Adapter<PresenceHistoryAdapter.ViewHolder> {

    private final List<AttendanceData> attendanceList;

    public PresenceHistoryAdapter(List<AttendanceData> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_presence, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceData data = attendanceList.get(position);

        holder.tvTitle.setText(data.event_name != null ? data.event_name : "-");
        holder.tvDate.setText(data.event_date != null ? data.event_date : "-");
        holder.tvTimeIn.setText(extractTimeFromEventTime(data.event_time, true));
        holder.tvTimeOut.setText(extractTimeFromEventTime(data.event_time, false));
        holder.tvUserTime.setText("Absen: " + (data.check_in_time != null ? data.check_in_time : "-"));

        // Hitung keterlambatan dibanding waktu selesai
        long lateMinutes = calculateLateMinutesFromEventTime(data.event_time, data.check_in_time);
        if (lateMinutes > 0) {
            holder.tvStatus.setText("Terlambat");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_late);
            holder.tvLateDetail.setVisibility(View.VISIBLE);
            holder.tvLateDetail.setText("Terlambat " + lateMinutes + " menit");
        } else {
            holder.tvStatus.setText("Hadir");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_present);
            holder.tvLateDetail.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvTimeIn, tvTimeOut, tvUserTime, tvStatus, tvLateDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTimeIn = itemView.findViewById(R.id.tv_time_in);
            tvTimeOut = itemView.findViewById(R.id.tv_time_out);
            tvUserTime = itemView.findViewById(R.id.tv_user_time);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvLateDetail = itemView.findViewById(R.id.tv_late_detail);
        }
    }

    private String extractTimeFromEventTime(String eventTime, boolean start) {
        if (eventTime != null && eventTime.contains("-")) {
            String[] parts = eventTime.split("-");
            return start ? parts[0].trim() : parts[1].trim();
        }
        return "-";
    }

    private long calculateLateMinutesFromEventTime(String eventTime, String checkInTime) {
        try {
            if (eventTime == null || checkInTime == null || !eventTime.contains("-")) return 0;

            String[] parts = eventTime.split("-");
            String startTimeStr = parts[0].trim();
            String endTimeStr = parts[1].trim();

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date startTime = sdf.parse(startTimeStr);
            Date endTime = sdf.parse(endTimeStr);
            Date checkIn = sdf.parse(checkInTime);

            // Kalau check-in masih di dalam range start - end, dianggap hadir tepat waktu
            if (!checkIn.after(endTime)) {
                return 0;
            }

            // Jika check-in lewat dari endTime, hitung selisih menit
            long diff = checkIn.getTime() - endTime.getTime();
            return TimeUnit.MILLISECONDS.toMinutes(diff);

        } catch (ParseException e) {
            return 0;
        }
    }
}

package com.inovarka.myormawa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.Meeting;

import java.util.List;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder> {

    private List<Meeting> meetingList;
    private OnReminderClick reminderClick;
    private Context context;

    public interface OnReminderClick {
        void onReminderClick(Meeting meeting, ImageView bellView);
    }

    public MeetingAdapter(Context context, List<Meeting> meetingList) {
        this.context = context;
        this.meetingList = meetingList;
    }

    public void setOnReminderClick(OnReminderClick r) {
        this.reminderClick = r;
    }

    public void updateList(List<Meeting> newList) {
        meetingList.clear();
        meetingList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meeting, parent, false);
        return new MeetingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingViewHolder holder, int position) {
        Meeting meeting = meetingList.get(position);

        holder.tvMeetingName.setText(meeting.getName());
        holder.tvAgenda.setText(meeting.getAgenda());
        holder.tvDate.setText(meeting.getDate());
        holder.tvTime.setText(meeting.getTimeRange());
        holder.tvLocation.setText(meeting.getLocation());

        // update bell icon state (filled if set)
        boolean set = com.inovarka.myormawa.utils.ReminderStorage.isReminderSet(context, meeting.getId());
        if (set) {
            holder.btnReminder.setImageResource(R.drawable.ic_bell_ring_filled); // sediakan drawable ic_bell_filled
            holder.btnReminder.setAlpha(1f);
        } else {
            holder.btnReminder.setImageResource(R.drawable.ic_bell_ring);
            holder.btnReminder.setAlpha(0.9f);
        }

        holder.btnReminder.setOnClickListener(v -> {
            if (reminderClick != null) reminderClick.onReminderClick(meeting, holder.btnReminder);
        });
    }

    @Override
    public int getItemCount() {
        return meetingList.size();
    }

    public static class MeetingViewHolder extends RecyclerView.ViewHolder {
        TextView tvMeetingName, tvAgenda, tvDate, tvTime, tvLocation;
        ImageView btnReminder;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMeetingName = itemView.findViewById(R.id.tv_meeting_name);
            tvAgenda = itemView.findViewById(R.id.tv_agenda);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvLocation = itemView.findViewById(R.id.tv_location);
            btnReminder = itemView.findViewById(R.id.btn_reminder);
        }
    }
}

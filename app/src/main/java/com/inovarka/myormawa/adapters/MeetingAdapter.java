package com.inovarka.myormawa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.Meeting;

import java.util.List;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder> {

    private List<Meeting> meetingList;

    public MeetingAdapter(List<Meeting> meetingList) {
        this.meetingList = meetingList;
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
    }

    @Override
    public int getItemCount() {
        return meetingList.size();
    }

    public static class MeetingViewHolder extends RecyclerView.ViewHolder {
        TextView tvMeetingName, tvAgenda, tvDate, tvTime, tvLocation;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMeetingName = itemView.findViewById(R.id.tv_meeting_name);
            tvAgenda = itemView.findViewById(R.id.tv_agenda);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvLocation = itemView.findViewById(R.id.tv_location);
        }
    }
}
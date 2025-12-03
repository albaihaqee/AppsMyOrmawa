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
import com.inovarka.myormawa.models.ReminderItem;
import com.inovarka.myormawa.utils.ReminderStorage;

import java.util.List;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.KegiatanViewHolder> {

    private List<Meeting> kegiatanList;
    private OnReminderClick reminderClick;
    private Context context;

    public interface OnReminderClick {
        void onReminderClick(Meeting kegiatan, ImageView bellView);
    }

    public MeetingAdapter(Context context, List<Meeting> kegiatanList) {
        this.context = context;
        this.kegiatanList = kegiatanList;
    }

    public void setOnReminderClick(OnReminderClick r) {
        this.reminderClick = r;
    }

    public void updateList(List<Meeting> newList) {
        kegiatanList.clear();
        kegiatanList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KegiatanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meeting, parent, false); // bisa pakai layout meeting
        return new KegiatanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KegiatanViewHolder holder, int position) {
        Meeting kegiatan = kegiatanList.get(position);

        holder.tvMeetingName.setText(kegiatan.getNama());
        holder.tvAgenda.setText(kegiatan.getAgenda());
        holder.tvDate.setText(kegiatan.getTanggal());
        holder.tvTime.setText(kegiatan.getWaktu());
        holder.tvLocation.setText(kegiatan.getLokasi());

        boolean set = ReminderStorage.isReminderSet(context, kegiatan.getId());
        if (set) {
            holder.btnReminder.setImageResource(R.drawable.ic_bell_ring_filled);
            holder.btnReminder.setAlpha(1f);
        } else {
            holder.btnReminder.setImageResource(R.drawable.ic_bell_ring);
            holder.btnReminder.setAlpha(0.9f);
        }

        holder.btnReminder.setOnClickListener(v -> {
            if (reminderClick != null) reminderClick.onReminderClick(kegiatan, holder.btnReminder);
        });
    }

    @Override
    public int getItemCount() {
        return kegiatanList.size();
    }

    public static class KegiatanViewHolder extends RecyclerView.ViewHolder {
        TextView tvMeetingName, tvAgenda, tvDate, tvTime, tvLocation;
        ImageView btnReminder;

        public KegiatanViewHolder(@NonNull View itemView) {
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

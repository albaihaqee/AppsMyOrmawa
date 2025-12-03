package com.inovarka.myormawa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.PresenceHistory;

import java.util.List;

public class PresenceHistoryAdapter extends RecyclerView.Adapter<PresenceHistoryAdapter.PresenceViewHolder> {

    private List<PresenceHistory> presenceList;

    public PresenceHistoryAdapter(List<PresenceHistory> presenceList) {
        this.presenceList = presenceList;
    }

    @NonNull
    @Override
    public PresenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_presence, parent, false);
        return new PresenceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PresenceViewHolder holder, int position) {
        PresenceHistory presence = presenceList.get(position);

        holder.tvTitle.setText(presence.getTitle());
        holder.tvDate.setText(presence.getDate());
        holder.tvTimeIn.setText(presence.getStartTime());
        holder.tvTimeOut.setText(presence.getEndTime());
        holder.tvUserTime.setText("Absen: " + presence.getUserTime());

        // Set status
        holder.tvStatus.setText(presence.getStatus());

        // ============================
        // STATUS HANDLING (2 opsi saja)
        // ============================

        if (presence.getStatus().equalsIgnoreCase("Hadir")) {

            // Warna hijau
            holder.tvStatus.setBackground(
                    ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_status_present)
            );
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));

            holder.tvLateDetail.setVisibility(View.GONE);

        } else if (presence.getStatus().equalsIgnoreCase("Terlambat")) {

            // Warna merah
            holder.tvStatus.setBackground(
                    ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_status_late)
            );
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));

            holder.tvLateDetail.setVisibility(View.VISIBLE);
            holder.tvLateDetail.setText(presence.getLateDetail());
        }
    }

    @Override
    public int getItemCount() {
        return presenceList.size();
    }

    public static class PresenceViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvUserTime, tvDate, tvTimeIn, tvTimeOut, tvStatus, tvLateDetail;

        public PresenceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvUserTime = itemView.findViewById(R.id.tv_user_time);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTimeIn = itemView.findViewById(R.id.tv_time_in);
            tvTimeOut = itemView.findViewById(R.id.tv_time_out);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvLateDetail = itemView.findViewById(R.id.tv_late_detail);
        }
    }
}


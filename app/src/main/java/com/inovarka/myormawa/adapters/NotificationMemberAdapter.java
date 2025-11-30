package com.inovarka.myormawa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.NotificationMember;

import java.util.ArrayList;
import java.util.List;

public class NotificationMemberAdapter extends RecyclerView.Adapter<NotificationMemberAdapter.ViewHolder> {

    private List<NotificationMember> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NotificationMember notification);
    }

    public NotificationMemberAdapter() {
        this.items = new ArrayList<>();
    }

    public void setItems(List<NotificationMember> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationMember item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvAgenda, tvDate, tvTime, tvLocation, tvPosted;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_meeting_title);
            tvAgenda = itemView.findViewById(R.id.tv_meeting_agenda);
            tvDate = itemView.findViewById(R.id.tv_meeting_date);
            tvTime = itemView.findViewById(R.id.tv_meeting_time);
            tvLocation = itemView.findViewById(R.id.tv_meeting_location);
            tvPosted = itemView.findViewById(R.id.tv_posted_time);
        }

        public void bind(NotificationMember n, OnItemClickListener listener) {
            tvTitle.setText(n.getTitle());
            tvAgenda.setText(n.getAgenda());
            tvDate.setText(n.getDate());
            tvTime.setText(n.getTime());
            tvLocation.setText(n.getLocation());
            tvPosted.setText(n.getPostedTime());

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(n);
            });
        }
    }
}

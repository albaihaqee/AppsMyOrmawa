package com.inovarka.myormawa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notifications;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Notification notification);
    }

    public NotificationAdapter() {
        this.notifications = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification, listener);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final View readIndicator;
        private final TextView txtTitle;
        private final TextView txtMessage;
        private final TextView txtTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            readIndicator = itemView.findViewById(R.id.view_read_indicator);
            txtTitle = itemView.findViewById(R.id.txt_notification_title);
            txtMessage = itemView.findViewById(R.id.txt_notification_message);
            txtTime = itemView.findViewById(R.id.txt_notification_time);
        }

        public void bind(Notification notification, OnItemClickListener listener) {
            txtTitle.setText(notification.getTitle());
            txtMessage.setText(notification.getMessage());
            txtTime.setText(notification.getTime());

            // Show/hide read indicator
            readIndicator.setVisibility(notification.isRead() ? View.INVISIBLE : View.VISIBLE);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(notification);
                }
            });
        }
    }
}
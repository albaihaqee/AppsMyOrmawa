package com.inovarka.myormawa.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
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

        // ADD — ambil badge status dari XML
        private final TextView txtStatus; // ADD

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            readIndicator = itemView.findViewById(R.id.view_read_indicator);
            txtTitle = itemView.findViewById(R.id.txt_notification_title);
            txtMessage = itemView.findViewById(R.id.txt_notification_message);
            txtTime = itemView.findViewById(R.id.txt_notification_time);

            txtStatus = itemView.findViewById(R.id.txt_notification_status); // ADD
        }

        public void bind(Notification notification, OnItemClickListener listener) {

            txtTitle.setText(notification.getTitle());
            txtMessage.setText(notification.getMessage());
            txtTime.setText(notification.getTime());

            // -----------------------------------------
            // ADD — Handle BADGE STATUS
            // -----------------------------------------
            if (notification.getStatus() != null && !notification.getStatus().isEmpty()) {

                txtStatus.setVisibility(View.VISIBLE);
                String status = notification.getStatus().toLowerCase();

                // Isi text status
                txtStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1));

                // Warna berdasarkan status
                switch (status) {
                    case "approved":
                        txtStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2ECC71"))); // hijau
                        break;

                    case "pending":
                        txtStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F1C40F"))); // kuning
                        break;

                    case "denied":
                    case "rejected":
                        txtStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E74C3C"))); // merah
                        break;

                    default:
                        txtStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7F7F7F"))); // abu
                }

            } else {
                txtStatus.setVisibility(View.GONE);
            }
            // -----------------------------------------

            // Show/hide read indicator
            readIndicator.setVisibility(notification.isRead() ? View.INVISIBLE : View.VISIBLE);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(notification);
            });
        }
    }
}

package com.inovarka.myormawa.adapters;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.ReminderItem;
import com.inovarka.myormawa.utils.ReminderStorage;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private List<ReminderItem> list;

    public ReminderAdapter(List<ReminderItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ReminderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meeting, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderAdapter.ViewHolder holder, int position) {
        ReminderItem item = list.get(position);

        holder.tvMeetingName.setText(item.getMeeting().getName());
        holder.tvAgenda.setText(item.getMeeting().getAgenda());
        holder.tvDate.setText(item.getMeeting().getDate());
        holder.tvTime.setText(item.getMeeting().getTimeRange());
        holder.tvLocation.setText(item.getMeeting().getLocation());

        holder.tvAgenda.append(" · Reminder " + item.getMinutesBefore() + "m sebelum");

        holder.btnReminder.setImageResource(R.drawable.ic_notification_filled);

        holder.btnReminder.setOnClickListener(v -> {
            showDeleteDialog(holder.itemView.getContext(), item, position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void showDeleteDialog(android.content.Context context, ReminderItem item, int position) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_delete); // kamu tinggal bikin filenya (aku siapin kalau mau)

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }

        MaterialButton btnNo = dialog.findViewById(R.id.btn_no);
        MaterialButton btnYes = dialog.findViewById(R.id.btn_yes);

        btnNo.setOnClickListener(v -> dialog.dismiss());

        btnYes.setOnClickListener(v -> {
            dialog.dismiss();

            // hapus dari storage
            ReminderStorage.removeReminder(context, item.getMeeting().getId());

            // hapus dari list RecyclerView
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
        });

        dialog.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMeetingName, tvAgenda, tvDate, tvTime, tvLocation;
        ImageView btnReminder;

        public ViewHolder(@NonNull View itemView) {
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

package com.inovarka.myormawa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.FormInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryOprecAdapter extends RecyclerView.Adapter<HistoryOprecAdapter.ViewHolder> {

    private Context context;
    private List<FormInfo> historyList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FormInfo formInfo);
    }

    public HistoryOprecAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.historyList = new ArrayList<>();
        this.listener = listener;
    }

    public void setHistoryList(List<FormInfo> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history_oprec, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FormInfo formInfo = historyList.get(position);

        // Load image
        if (formInfo.getGambarUrl() != null && !formInfo.getGambarUrl().isEmpty()) {
            Glide.with(context)
                    .load(formInfo.getGambarUrl())
                    .placeholder(R.drawable.ic_location)
                    .error(R.drawable.ic_location)
                    .into(holder.imgPoster);
        } else {
            holder.imgPoster.setImageResource(R.drawable.ic_location);
        }

        // Set title
        holder.txtTitle.setText(formInfo.getJudul());

        // Set ormawa name
        holder.txtOrmawa.setText(formInfo.getNamaOrmawa());

        // Format and set submitted date
        holder.txtSubmittedDate.setText("Didaftar: " + formatDate(formInfo.getSubmittedAt()));

        // Set status badge
        String status = formInfo.getStatus() != null ? formInfo.getStatus().toLowerCase() : "pending";
        switch (status) {
            case "approved":
                holder.txtStatus.setText("Approved");
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
                holder.txtStatus.setBackgroundResource(R.drawable.shape_status_approved);
                break;
            case "rejected":
                holder.txtStatus.setText("Rejected");
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
                holder.txtStatus.setBackgroundResource(R.drawable.shape_status_rejected);
                break;
            case "pending":
            default:
                holder.txtStatus.setText("Pending");
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.orange));
                holder.txtStatus.setBackgroundResource(R.drawable.shape_status_pending);
                break;
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(formInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
            Date date = inputFormat.parse(dateString);
            return date != null ? outputFormat.format(date) : dateString;
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster, imgArrow;
        TextView txtStatus, txtTitle, txtOrmawa, txtSubmittedDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.img_oprec_poster);
            imgArrow = itemView.findViewById(R.id.img_arrow_right);
            txtStatus = itemView.findViewById(R.id.txt_status_badge);
            txtTitle = itemView.findViewById(R.id.txt_oprec_title);
            txtOrmawa = itemView.findViewById(R.id.txt_ormawa_name);
            txtSubmittedDate = itemView.findViewById(R.id.txt_submitted_date);
        }
    }
}
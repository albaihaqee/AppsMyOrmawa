package com.inovarka.myormawa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.FormInfo;

import java.util.ArrayList;
import java.util.List;

public class FormListAdapter extends RecyclerView.Adapter<FormListAdapter.ViewHolder> {

    private List<FormInfo> formList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FormInfo formInfo);
    }

    public FormListAdapter() {
        this.formList = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setFormList(List<FormInfo> formList) {
        this.formList = formList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_oprec, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FormInfo form = formList.get(position);
        holder.bind(form, listener);
    }

    @Override
    public int getItemCount() {
        return formList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgPoster;
        private final TextView txtStatus;
        private final TextView txtTitle;
        private final TextView txtOrganization;
        private final TextView txtDeadline;
        private final TextView txtParticipants;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.img_oprec_poster);
            txtStatus = itemView.findViewById(R.id.txt_oprec_status);
            txtTitle = itemView.findViewById(R.id.txt_oprec_title);
            txtOrganization = itemView.findViewById(R.id.txt_oprec_organization);
            txtDeadline = itemView.findViewById(R.id.txt_oprec_deadline);
            txtParticipants = itemView.findViewById(R.id.txt_oprec_participants);
        }

        public void bind(FormInfo form, OnItemClickListener listener) {
            txtTitle.setText(form.getJudul());
            txtOrganization.setText(form.getNamaOrmawa());
            txtDeadline.setText("Dibuat: " + form.getCreatedAt());
            txtParticipants.setText(form.getParticipantsText());

            // Set status badge
            if (form.isActive()) {
                txtStatus.setText("Aktif");
                txtStatus.setBackgroundResource(R.drawable.shape_badge_active);
            } else {
                txtStatus.setText("Ditutup");
                txtStatus.setBackgroundResource(R.drawable.shape_badge_inactive);
            }

            // Load poster image with Glide
            if (form.getGambarUrl() != null && !form.getGambarUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(form.getGambarUrl())
                        .centerCrop()
                        .placeholder(R.drawable.ic_home)
                        .error(R.drawable.ic_home)
                        .into(imgPoster);
            } else {
                imgPoster.setImageResource(R.drawable.ic_home);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(form);
                }
            });
        }
    }
}
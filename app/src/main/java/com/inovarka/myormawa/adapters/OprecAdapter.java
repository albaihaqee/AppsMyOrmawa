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
import com.inovarka.myormawa.models.Oprec;

import java.util.ArrayList;
import java.util.List;

public class OprecAdapter extends RecyclerView.Adapter<OprecAdapter.ViewHolder> {

    private List<Oprec> oprecList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Oprec oprec);
    }

    public OprecAdapter() {
        this.oprecList = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOprecList(List<Oprec> oprecList) {
        this.oprecList = oprecList;
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
        Oprec oprec = oprecList.get(position);
        holder.bind(oprec, listener);
    }

    @Override
    public int getItemCount() {
        return oprecList.size();
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

        public void bind(Oprec oprec, OnItemClickListener listener) {
            txtTitle.setText(oprec.getTitle());
            txtOrganization.setText(oprec.getOrganization());
            txtDeadline.setText(oprec.getDeadline());
            txtParticipants.setText(oprec.getParticipantsText());

            // Set status badge
            if (oprec.isActive()) {
                txtStatus.setText("Aktif");
                txtStatus.setBackgroundResource(R.drawable.shape_badge_active);
            } else {
                txtStatus.setText("Ditutup");
                txtStatus.setBackgroundResource(R.drawable.shape_badge_inactive);
            }

            // Load poster image with Glide
            if (oprec.getPosterUrl() != null && !oprec.getPosterUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(oprec.getPosterUrl())
                        .centerCrop()
                        .placeholder(R.drawable.ic_home)
                        .error(R.drawable.ic_home)
                        .into(imgPoster);
            } else {
                imgPoster.setImageResource(R.drawable.ic_home);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(oprec);
                }
            });
        }
    }
}
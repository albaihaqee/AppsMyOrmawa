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
import com.inovarka.myormawa.models.Competition;

import java.util.ArrayList;
import java.util.List;

public class CompetitionAdapter extends RecyclerView.Adapter<CompetitionAdapter.ViewHolder> {

    private List<Competition> competitionList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Competition competition);
    }

    public CompetitionAdapter() {
        this.competitionList = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setCompetitionList(List<Competition> competitionList) {
        this.competitionList = competitionList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_competition, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Competition competition = competitionList.get(position);
        holder.bind(competition, listener);
    }

    @Override
    public int getItemCount() {
        return competitionList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgPoster;
        private final TextView txtTitle;
        private final TextView txtOrganizer;
        private final TextView txtPeriod;
        private final TextView txtPrize;
        private final TextView txtDeadline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.img_competition_poster);
            txtTitle = itemView.findViewById(R.id.txt_competition_title);
            txtOrganizer = itemView.findViewById(R.id.txt_competition_organizer);
            txtPeriod = itemView.findViewById(R.id.txt_competition_period);
            txtPrize = itemView.findViewById(R.id.txt_competition_prize);
            txtDeadline = itemView.findViewById(R.id.txt_competition_deadline);
        }

        public void bind(Competition competition, OnItemClickListener listener) {
            txtTitle.setText(competition.getTitle());
            txtOrganizer.setText(competition.getOrganizer());
            txtPeriod.setText(competition.getRegistrationPeriod());
            txtPrize.setText(competition.getPrize());
            txtDeadline.setText(competition.getTeamSizeText());

            // Load poster image with Glide
            if (competition.getPosterUrl() != null && !competition.getPosterUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(competition.getPosterUrl())
                        .centerCrop()
                        .placeholder(R.drawable.ic_home)
                        .error(R.drawable.ic_home)
                        .into(imgPoster);
            } else {
                imgPoster.setImageResource(R.drawable.ic_home);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(competition);
                }
            });
        }
    }
}
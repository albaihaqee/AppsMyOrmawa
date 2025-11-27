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
import com.inovarka.myormawa.models.Event;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> eventList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }

    public EventAdapter() {
        this.eventList = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event, listener);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgPoster;
        private final TextView txtCategory;
        private final TextView txtTitle;
        private final TextView txtLocation;
        private final TextView txtDate;
        private final TextView txtParticipants;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.img_event_poster);
            txtCategory = itemView.findViewById(R.id.txt_event_category);
            txtTitle = itemView.findViewById(R.id.txt_event_title);
            txtLocation = itemView.findViewById(R.id.txt_event_location);
            txtDate = itemView.findViewById(R.id.txt_event_date);
            txtParticipants = itemView.findViewById(R.id.txt_event_participants);
        }

        public void bind(Event event, OnItemClickListener listener) {
            txtCategory.setText(event.getCategory());
            txtTitle.setText(event.getTitle());
            txtLocation.setText(event.getLocation());
            txtDate.setText(event.getDate());
            txtParticipants.setText(event.getParticipantsText());

            if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(event.getPosterUrl())
                        .centerCrop()
                        .placeholder(R.drawable.ic_home)
                        .error(R.drawable.ic_home)
                        .into(imgPoster);
            } else {
                imgPoster.setImageResource(R.drawable.ic_home);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(event);
                }
            });
        }
    }
}
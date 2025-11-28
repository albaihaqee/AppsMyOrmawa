package com.inovarka.myormawa.adapters;

import android.graphics.Color;
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
import com.inovarka.myormawa.models.EventMember;

import java.util.List;

public class EventMemberAdapter extends RecyclerView.Adapter<EventMemberAdapter.EventViewHolder> {

    private List<EventMember> eventList;
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(EventMember event);
    }

    public EventMemberAdapter(List<EventMember> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventMember event = eventList.get(position);

        holder.txtEventTitle.setText(event.getTitle());
        holder.txtEventCategory.setText(event.getCategory());
        holder.txtEventLocation.setText(event.getLocation());
        holder.txtEventDate.setText(event.getDate());
        holder.txtEventParticipants.setText(event.getParticipantsText());

        // Load poster image using Glide
        if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(event.getPosterUrl())
                    .placeholder(R.drawable.placeholder_event)
                    .error(R.drawable.placeholder_event)
                    .into(holder.imgEventPoster);
        } else {
            holder.imgEventPoster.setImageResource(R.drawable.placeholder_event);
        }

        // Set category badge color
        setCategoryBadgeColor(holder.txtEventCategory, event.getCategory());

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventClick(event);
            }
        });
    }

    private void setCategoryBadgeColor(TextView badge, String category) {
        int backgroundColor;
        switch (category.toLowerCase()) {
            case "workshop":
                backgroundColor = Color.parseColor("#FF6B6B");
                break;
            case "seminar":
                backgroundColor = Color.parseColor("#4ECDC4");
                break;
            case "kompetisi":
                backgroundColor = Color.parseColor("#FFD93D");
                break;
            case "pelatihan":
                backgroundColor = Color.parseColor("#95E1D3");
                break;
            default:
                backgroundColor = Color.parseColor("#2C4EEF");
                break;
        }
        badge.setBackgroundColor(backgroundColor);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateEvents(List<EventMember> newEvents) {
        this.eventList = newEvents;
        notifyDataSetChanged();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEventPoster, imgArrowRight;
        TextView txtEventCategory, txtEventTitle, txtEventLocation,
                txtEventDate, txtEventParticipants;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEventPoster = itemView.findViewById(R.id.img_event_poster);
            imgArrowRight = itemView.findViewById(R.id.img_arrow_right);
            txtEventCategory = itemView.findViewById(R.id.txt_event_category);
            txtEventTitle = itemView.findViewById(R.id.txt_event_title);
            txtEventLocation = itemView.findViewById(R.id.txt_event_location);
            txtEventDate = itemView.findViewById(R.id.txt_event_date);
            txtEventParticipants = itemView.findViewById(R.id.txt_event_participants);
        }
    }
}
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
                .inflate(R.layout.item_event_member, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventMember event = eventList.get(position);

        holder.txtEventTitle.setText(event.getTitle());
        holder.txtEventLocation.setText(event.getLocation());
        holder.txtEventDate.setText(event.getDisplayDate());

        // Load poster
        Glide.with(holder.itemView.getContext())
                .load(event.getPosterUrl())
                .placeholder(R.drawable.placeholder_event)
                .error(R.drawable.placeholder_event)
                .into(holder.imgEventPoster);

        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
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
        ImageView imgEventPoster;
        TextView txtEventTitle, txtEventLocation, txtEventDate;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEventPoster = itemView.findViewById(R.id.img_event_poster);
            txtEventTitle = itemView.findViewById(R.id.txt_event_title);
            txtEventLocation = itemView.findViewById(R.id.txt_event_location);
            txtEventDate = itemView.findViewById(R.id.txt_event_date);
        }
    }
}

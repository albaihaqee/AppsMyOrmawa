package com.inovarka.myormawa.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.Scholarship;

import java.util.ArrayList;
import java.util.List;

public class ScholarshipAdapter extends RecyclerView.Adapter<ScholarshipAdapter.ViewHolder> {

    private static final String TAG = "ScholarshipAdapter";
    private List<Scholarship> scholarshipList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Scholarship scholarship);
    }

    public ScholarshipAdapter() {
        this.scholarshipList = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setScholarshipList(List<Scholarship> scholarshipList) {
        this.scholarshipList = scholarshipList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scholarship, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Scholarship scholarship = scholarshipList.get(position);
        holder.bind(scholarship, listener);
    }

    @Override
    public int getItemCount() {
        return scholarshipList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgPoster;
        private final TextView txtTitle;
        private final TextView txtProvider;
        private final TextView txtDeadline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.img_scholarship_poster);
            txtTitle = itemView.findViewById(R.id.txt_scholarship_title);
            txtProvider = itemView.findViewById(R.id.txt_scholarship_provider);
            txtDeadline = itemView.findViewById(R.id.txt_scholarship_deadline);
        }

        public void bind(Scholarship scholarship, OnItemClickListener listener) {
            txtTitle.setText(scholarship.getTitle());
            txtProvider.setText(scholarship.getProvider());
            txtDeadline.setText(scholarship.getDeadline());

            // Load poster dengan Glide
            String posterUrl = scholarship.getPosterUrl();

            Log.d(TAG, "Loading poster for: " + scholarship.getTitle());
            Log.d(TAG, "Poster URL: " + posterUrl);

            if (posterUrl != null && !posterUrl.isEmpty()) {
                Log.d(TAG, "Attempting to load poster from: " + posterUrl);

                Glide.with(itemView.getContext())
                        .load(posterUrl)
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable cache untuk testing
                        .skipMemoryCache(true) // Skip memory cache
                        .placeholder(R.drawable.ic_home)
                        .error(R.drawable.ic_home)
                        .centerCrop()
                        .into(imgPoster);

                Log.d(TAG, "Glide loading initiated for: " + posterUrl);
            } else {
                Log.w(TAG, "Poster URL is null or empty for: " + scholarship.getTitle());
                imgPoster.setImageResource(R.drawable.ic_home);
            }

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(scholarship);
                }
            });
        }
    }
}
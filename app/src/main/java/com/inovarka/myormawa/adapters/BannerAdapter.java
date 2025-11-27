package com.inovarka.myormawa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.Banner;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<Banner> bannerList;
    private OnBannerClickListener listener;

    public interface OnBannerClickListener {
        void onBannerClick(Banner banner, int position);
    }

    public BannerAdapter(List<Banner> bannerList) {
        this.bannerList = bannerList;
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Banner banner = bannerList.get(position);

        // Load image
        if (banner.isFromUrl()) {
            Glide.with(holder.itemView.getContext())
                    .load(banner.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_home)
                    .error(R.drawable.ic_home)
                    .into(holder.imgBanner);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(banner.getImageResource())
                    .centerCrop()
                    .into(holder.imgBanner);
        }

        // Handle click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBannerClick(banner, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bannerList != null ? bannerList.size() : 0;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.img_banner);
        }
    }
}
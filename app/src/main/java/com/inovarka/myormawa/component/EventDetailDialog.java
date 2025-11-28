package com.inovarka.myormawa.component;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.Event;
import com.inovarka.myormawa.models.EventMember;

public class EventDetailDialog extends Dialog {

    private EventMember event;
    private ImageView imgDialogPoster, btnCloseDialog;
    private TextView tvDialogCategory, tvDialogTitle, tvDialogDate, tvDialogTime,
            tvDialogLocation, tvDialogParticipants, tvDialogDescription;


    public EventDetailDialog(@NonNull Context context, EventMember event) {
        super(context);
        this.event = event;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_event_member_detail);

        // Set dialog properties
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        initViews();
        setupData();
        setupListeners();
    }

    private void initViews() {
        imgDialogPoster = findViewById(R.id.img_dialog_poster);
        btnCloseDialog = findViewById(R.id.btn_close_dialog);
        tvDialogCategory = findViewById(R.id.tv_dialog_category);
        tvDialogTitle = findViewById(R.id.tv_dialog_title);
        tvDialogDate = findViewById(R.id.tv_dialog_date);
        tvDialogTime = findViewById(R.id.tv_dialog_time);
        tvDialogLocation = findViewById(R.id.tv_dialog_location);
        tvDialogParticipants = findViewById(R.id.tv_dialog_participants);
        tvDialogDescription = findViewById(R.id.tv_dialog_description);
    }

    private void setupData() {
        // Load poster
        if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
            Glide.with(getContext())
                    .load(event.getPosterUrl())
                    .placeholder(R.drawable.placeholder_event)
                    .error(R.drawable.placeholder_event)
                    .into(imgDialogPoster);
        } else {
            imgDialogPoster.setImageResource(R.drawable.placeholder_event);
        }

        // Set category badge color
        setCategoryBadgeColor(tvDialogCategory, event.getCategory());

        // Set data
        tvDialogCategory.setText(event.getCategory());
        tvDialogTitle.setText(event.getTitle());
        tvDialogDate.setText(event.getDate());
        tvDialogTime.setText(event.getTimeRange() + " WIB");
        tvDialogLocation.setText(event.getLocation());
        tvDialogParticipants.setText(event.getParticipants() + " peserta terdaftar");
        tvDialogDescription.setText(event.getDescription());
    }

    private void setupListeners() {
        btnCloseDialog.setOnClickListener(v -> dismiss());

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
}
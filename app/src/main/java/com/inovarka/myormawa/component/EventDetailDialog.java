package com.inovarka.myormawa.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.EventMember;

public class EventDetailDialog extends Dialog {

    public EventDetailDialog(Context context, EventMember event) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_event_member_detail);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = getWindow();
        if (window != null) {
            window.setLayout((int)(context.getResources().getDisplayMetrics().widthPixels * 0.9),
                    WindowManager.LayoutParams.WRAP_CONTENT);

        ImageView imgPoster   = findViewById(R.id.img_dialog_poster);
        TextView txtTitle     = findViewById(R.id.tv_dialog_title);
        TextView txtLocation  = findViewById(R.id.tv_dialog_location);
        TextView txtDate      = findViewById(R.id.tv_dialog_date);
        TextView txtTime      = findViewById(R.id.tv_dialog_time);
        TextView txtDesc      = findViewById(R.id.tv_dialog_description);
        ImageView btnClose    = findViewById(R.id.btn_close_dialog);

            int dialogWidth = (int)(context.getResources().getDisplayMetrics().widthPixels * 0.9);
            int posterHeight = (dialogWidth * 4) / 3; // 3:4 ratio
            imgPoster.getLayoutParams().width = dialogWidth;
            imgPoster.getLayoutParams().height = posterHeight;
            imgPoster.requestLayout();

            // Load poster
            Glide.with(context)
                    .load(event.getPosterUrl())
                    .placeholder(R.drawable.placeholder_event)
                    .into(imgPoster);
        // Set data
        txtTitle.setText(event.getTitle());
        txtLocation.setText(event.getLocation());

        // Tanggal gabungan
        txtDate.setText(event.getDisplayDate());

        // Waktu
        txtTime.setText(event.getWaktu_mulai() + " - " + event.getWaktu_selesai());

        // Deskripsi
        txtDesc.setText(event.getDescription());

        btnClose.setOnClickListener(v -> dismiss());
    }
} }

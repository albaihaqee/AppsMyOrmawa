package com.inovarka.myormawa.adapters;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.Document;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private List<Document> documentList;

    public DocumentAdapter(List<Document> documentList) {
        this.documentList = documentList;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documentList.get(position);

        holder.tvDocumentName.setText(document.getName());
        holder.tvDocumentType.setText(document.getType().toUpperCase());
        holder.tvFileSize.setText(document.getFileSize());
        holder.tvUploadDate.setText(document.getUploadDate());

        // Set icon dan background color
        holder.ivDocumentIcon.setImageResource(document.getIconResource());
        holder.layoutIcon.setBackgroundResource(document.getBackgroundColor());

        // Click listener untuk menampilkan dialog
        holder.itemView.setOnClickListener(v -> showDetailDialog(v, document));
        holder.btnMore.setOnClickListener(v -> showDetailDialog(v, document));
    }

    private void showDetailDialog(View view, Document document) {
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_document_detail);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Initialize dialog views
        ImageView btnClose = dialog.findViewById(R.id.btn_close_dialog);
        ImageView ivDialogIcon = dialog.findViewById(R.id.iv_dialog_icon);
        LinearLayout layoutDialogIcon = dialog.findViewById(R.id.iv_dialog_icon).getParent() instanceof LinearLayout
                ? (LinearLayout) dialog.findViewById(R.id.iv_dialog_icon).getParent() : null;
        TextView tvDialogDocumentName = dialog.findViewById(R.id.tv_dialog_document_name);
        TextView tvDialogDocumentType = dialog.findViewById(R.id.tv_dialog_document_type);
        TextView tvDialogFileSize = dialog.findViewById(R.id.tv_dialog_file_size);
        TextView tvDialogUploadDate = dialog.findViewById(R.id.tv_dialog_upload_date);
        CardView btnDownload = dialog.findViewById(R.id.btn_download);

        // Set data
        ivDialogIcon.setImageResource(document.getIconResource());
        if (layoutDialogIcon != null) {
            layoutDialogIcon.setBackgroundResource(document.getBackgroundColor());
        }
        tvDialogDocumentName.setText(document.getName());
        tvDialogDocumentType.setText(document.getType().toUpperCase());
        tvDialogFileSize.setText(document.getFileSize());
        tvDialogUploadDate.setText(document.getUploadDate());

        // Close button
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Download button
        btnDownload.setOnClickListener(v -> {
            // Implementasi download
            if (document.getUrl() != null && !document.getUrl().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(document.getUrl()));
                view.getContext().startActivity(intent);
            } else {
                Toast.makeText(view.getContext(), "Mengunduh: " + document.getName(), Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public void updateList(List<Document> newList) {
        documentList.clear();
        documentList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutIcon;
        ImageView ivDocumentIcon, btnMore;
        TextView tvDocumentName, tvDocumentType, tvFileSize, tvUploadDate;


        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutIcon = itemView.findViewById(R.id.layout_icon);
            ivDocumentIcon = itemView.findViewById(R.id.iv_document_icon);
            btnMore = itemView.findViewById(R.id.btn_more);
            tvDocumentName = itemView.findViewById(R.id.tv_document_name);
            tvDocumentType = itemView.findViewById(R.id.tv_document_type);
            tvFileSize = itemView.findViewById(R.id.tv_file_size);
            tvUploadDate = itemView.findViewById(R.id.tv_upload_date);
        }
    }
}
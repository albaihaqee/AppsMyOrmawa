package com.inovarka.myormawa.adapters;

import android.app.DownloadManager;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
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
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.Document;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private final List<Document> documentList;
    private final Context context;

    public DocumentAdapter(Context context, List<Document> documentList) {
        this.context = context;
        this.documentList = documentList;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documentList.get(position);

        holder.tvDocumentName.setText(document.getName() != null ? document.getName() : "-");
        holder.tvDocumentType.setText(document.getType() != null ? document.getType().toUpperCase() : "-");
        holder.tvFileSize.setText(document.getFileSize() != null ? document.getFileSize() : "-");
        holder.tvUploadDate.setText(document.getUploadDate() != null ? document.getUploadDate() : "-");
        holder.ivDocumentIcon.setImageResource(document.getIconResource());
        holder.layoutIcon.setBackgroundResource(document.getBackgroundColor());

        // Klik item untuk tampilkan dialog
        holder.itemView.setOnClickListener(v -> showDetailDialog(document));
    }

    @Override
    public int getItemCount() {
        return documentList != null ? documentList.size() : 0;
    }

    public void updateList(List<Document> newList) {
        documentList.clear();
        if (newList != null) documentList.addAll(newList);
        notifyDataSetChanged();
    }

    private void showDetailDialog(Document document) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_document_detail);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageView btnClose = dialog.findViewById(R.id.btn_close_dialog);
        ImageView ivDialogIcon = dialog.findViewById(R.id.iv_dialog_icon);
        LinearLayout layoutDialogIcon = dialog.findViewById(R.id.layout_dialog_icon);
        TextView tvDialogDocumentName = dialog.findViewById(R.id.tv_dialog_document_name);
        TextView tvDialogDocumentType = dialog.findViewById(R.id.tv_dialog_document_type);
        TextView tvDialogFileSize = dialog.findViewById(R.id.tv_dialog_file_size);
        TextView tvDialogUploadDate = dialog.findViewById(R.id.tv_dialog_upload_date);
        CardView btnDownload = dialog.findViewById(R.id.btn_download);

        ivDialogIcon.setImageResource(document.getIconResource());
        layoutDialogIcon.setBackgroundResource(document.getBackgroundColor());
        tvDialogDocumentName.setText(document.getName());
        tvDialogDocumentType.setText(document.getType().toUpperCase());
        tvDialogFileSize.setText(document.getFileSize());
        tvDialogUploadDate.setText(document.getUploadDate());

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnDownload.setOnClickListener(v -> {
            dialog.dismiss();
            startDownload(document);
        });

        dialog.show();
    }

    private void startDownload(Document document) {
        if (document.getUrl() == null || document.getUrl().isEmpty()) {
            Toast.makeText(context, "Tidak ada URL untuk dokumen", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil nama file asli dengan ekstensi
        String fileName = document.getFilePath();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(document.getUrl()));
        request.setTitle(fileName);
        request.setDescription("Mengunduh dokumen...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);

        Toast.makeText(context, "Download dimulai...", Toast.LENGTH_SHORT).show();
    }


    static class DocumentViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutIcon;
        ImageView ivDocumentIcon;
        TextView tvDocumentName, tvDocumentType, tvFileSize, tvUploadDate;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutIcon = itemView.findViewById(R.id.layout_icon);
            ivDocumentIcon = itemView.findViewById(R.id.iv_document_icon);
            tvDocumentName = itemView.findViewById(R.id.tv_document_name);
            tvDocumentType = itemView.findViewById(R.id.tv_document_type);
            tvFileSize = itemView.findViewById(R.id.tv_file_size);
            tvUploadDate = itemView.findViewById(R.id.tv_upload_date);
        }
    }
}

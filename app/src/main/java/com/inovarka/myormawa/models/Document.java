package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;
import com.inovarka.myormawa.R;

public class Document {

    @SerializedName("id")
    private String id;

    @SerializedName("nama_dokumen")
    private String name;

    @SerializedName("jenis_dokumen")
    private String type;

    @SerializedName("tanggal_upload")
    private String uploadDate;

    @SerializedName("ukuran_file")
    private String fileSize;

    @SerializedName("download_url")
    private String url;

    @SerializedName("file_path")
    private String filePath;

    public Document(String id, String name, String type, String uploadDate, String fileSize, String url, String filePath) {
        this.id = id != null ? id : "";
        this.name = name != null ? name : "-";
        this.type = type != null ? type : "-";
        this.uploadDate = uploadDate != null ? uploadDate : "-";
        this.fileSize = fileSize != null ? fileSize : "-";
        this.url = url != null ? url : "";
        this.filePath = filePath != null ? filePath : "";
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type != null ? type : "-"; }
    public String getUploadDate() { return uploadDate; }
    public String getFileSize() { return fileSize; }
    public String getUrl() { return url; }
    public String getFilePath() { return filePath; }

    public int getIconResource() {
        String t = getType().toUpperCase();
        switch (t) {
            case "PDF": return R.drawable.ic_document;
            case "DOC":
            case "DOCX": return R.drawable.ic_document;
            case "XLS":
            case "XLSX": return R.drawable.ic_document;
            case "PPT":
            case "PPTX": return R.drawable.ic_document;
            default: return R.drawable.ic_document;
        }
    }

    public int getBackgroundColor() {
        String t = getType().toUpperCase();
        switch (t) {
            case "PDF": return R.color.red_500;
            case "DOC":
            case "DOCX": return R.color.blue_500;
            case "XLS":
            case "XLSX": return R.color.green_500;
            case "PPT":
            case "PPTX": return R.color.orange_500;
            default: return R.color.gray_500;
        }
    }
}

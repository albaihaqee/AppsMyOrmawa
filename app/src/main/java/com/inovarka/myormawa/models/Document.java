package com.inovarka.myormawa.models;

import com.inovarka.myormawa.R;

public class Document {
    private String id;
    private String name;
    private String type;
    private String uploadDate;
    private String fileSize;
    private String url;

    public Document(String id, String name, String type, String uploadDate, String fileSize, String url) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.uploadDate = uploadDate;
        this.fileSize = fileSize;
        this.url = url;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getUploadDate() { return uploadDate; }
    public String getFileSize() { return fileSize; }
    public String getUrl() { return url; }

    public int getIconResource() {
        switch (type.toUpperCase()) {
            case "PDF":
                return R.drawable.ic_document;
            case "DOC":
            case "DOCX":
                return R.drawable.ic_document;
            case "XLS":
            case "XLSX":
                return R.drawable.ic_document;
            case "PPT":
            case "PPTX":
                return R.drawable.ic_document;
            default:
                return R.drawable.ic_document;
        }
    }

    public int getBackgroundColor() {
        switch (type.toUpperCase()) {
            case "PDF":
                return R.color.red_500;
            case "DOC":
            case "DOCX":
                return R.color.blue_500;
            case "XLS":
            case "XLSX":
                return R.color.green_500;
            case "PPT":
            case "PPTX":
                return R.color.orange_500;
            default:
                return R.color.gray_500;
        }
    }
}
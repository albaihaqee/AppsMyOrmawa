package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class FileUploadData {
    @SerializedName("file_name")
    private String fileName;

    @SerializedName("file_url")
    private String fileUrl;

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
}

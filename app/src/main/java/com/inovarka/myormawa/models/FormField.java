package com.inovarka.myormawa.models;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FormField {
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_NUMBER = "number";
    public static final String TYPE_TEXTAREA = "textarea";
    public static final String TYPE_FILE = "file";
    public static final String TYPE_RADIO = "radio";
    public static final String TYPE_SELECT = "select";

    @SerializedName("id")
    private String id;

    @SerializedName("nama")
    private String nama;

    @SerializedName("tipe")
    private String tipe;

    @SerializedName("label")
    private String label;

    @SerializedName("options")
    private List<String> options;

    // For storing user input
    private String value;

    private Uri localFileUri;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getTipe() { return tipe; }
    public void setTipe(String tipe) { this.tipe = tipe; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public Uri getLocalFileUri() { return localFileUri; }
    public void setLocalFileUri(Uri localFileUri) { this.localFileUri = localFileUri; }
    public boolean isRequired() {
        // Bisa ditambahkan logika untuk field wajib
        return true;
    }
}

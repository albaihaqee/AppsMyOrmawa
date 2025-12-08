package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FormInfo {
    @SerializedName("id")
    private String id;

    @SerializedName("judul")
    private String judul;

    @SerializedName("deskripsi")
    private String deskripsi;

    @SerializedName("gambar")
    private String gambar;

    @SerializedName("gambar_url")
    private String gambarUrl;

    @SerializedName("status")
    private String status;

    @SerializedName("jenis_form")
    private String jenisForm;

    @SerializedName("nama_ormawa")
    private String namaOrmawa;

    @SerializedName("ormawa_id")
    private String ormawaId;

    @SerializedName("total_submissions")
    private int totalSubmissions;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("submitted_at")
    private String submittedAt;

    @SerializedName("fields")
    private List<FormField> fields;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getGambar() { return gambar; }
    public void setGambar(String gambar) { this.gambar = gambar; }

    public String getGambarUrl() { return gambarUrl; }
    public void setGambarUrl(String gambarUrl) { this.gambarUrl = gambarUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getJenisForm() { return jenisForm; }
    public void setJenisForm(String jenisForm) { this.jenisForm = jenisForm; }

    public String getNamaOrmawa() { return namaOrmawa; }
    public void setNamaOrmawa(String namaOrmawa) { this.namaOrmawa = namaOrmawa; }

    public String getOrmawaId() { return ormawaId; }
    public void setOrmawaId(String ormawaId) { this.ormawaId = ormawaId; }

    public int getTotalSubmissions() { return totalSubmissions; }
    public void setTotalSubmissions(int totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }

    public List<FormField> getFields() { return fields; }
    public void setFields(List<FormField> fields) { this.fields = fields; }

    public String getParticipantsText() {
        return totalSubmissions + " pendaftar";
    }

    public boolean isActive() {
        return "published".equals(status);
    }
}
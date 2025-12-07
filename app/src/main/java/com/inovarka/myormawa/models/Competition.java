package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class Competition {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("organizer")
    private String organizer;

    @SerializedName("description")
    private String description;

    @SerializedName("registrationPeriod")
    private String registrationPeriod;

    @SerializedName("tgl_mulai")
    private String tglMulai;

    @SerializedName("tgl_selesai")
    private String tglSelesai;

    @SerializedName("posterUrl")
    private String posterUrl;

    @SerializedName("guideBookUrl")
    private String guideBookUrl;

    @SerializedName("guideBookFilename")
    private String guideBookFilename;
    @SerializedName("created_at")
    private String createdAt;

    public String getCreatedAt() {
        return createdAt;
    }


    // Constructor kosong
    public Competition() {}

    // Constructor lengkap untuk backward compatibility
    public Competition(String id, String title, String organizer, String registrationPeriod,
                       String description, String posterUrl, String guideBookUrl) {
        this.id = id;
        this.title = title;
        this.organizer = organizer;
        this.registrationPeriod = registrationPeriod;
        this.description = description;
        this.posterUrl = posterUrl;
        this.guideBookUrl = guideBookUrl;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOrganizer() {
        return organizer;
    }

    public String getDescription() {
        return description;
    }

    public String getRegistrationPeriod() {
        return registrationPeriod;
    }

    public String getTglMulai() {
        return tglMulai;
    }

    public String getTglSelesai() {
        return tglSelesai;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getGuideBookUrl() {
        return guideBookUrl;
    }

    public String getGuideBookFilename() {
        return guideBookFilename;
    }

    // Helper methods
    public boolean hasGuideBook() {
        return guideBookUrl != null && !guideBookUrl.isEmpty();
    }

    // Badge status berdasarkan tanggal
    public String getStatusBadge() {
        // Format: "Aktif" atau "Berakhir"
        // Logic ini bisa dikembangkan dengan date comparison
        return "Aktif"; // Default
    }

    public boolean isActive() {
        // Akan di-filter di API, jadi semua data yang datang sudah upcoming
        return true;
    }
}
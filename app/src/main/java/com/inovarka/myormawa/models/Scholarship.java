package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class Scholarship {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("provider")
    private String provider;

    @SerializedName("description")
    private String description;

    @SerializedName("deadline")
    private String deadline;

    @SerializedName("deadlineRaw")
    private String deadlineRaw;

    @SerializedName("posterUrl")
    private String posterUrl;

    @SerializedName("guideBookUrl")
    private String guideBookUrl;

    @SerializedName("guideBookFilename")
    private String guideBookFilename;

    // Constructor kosong
    public Scholarship() {}

    // Constructor lengkap untuk backward compatibility
    public Scholarship(String id, String title, String provider, String deadline,
                       String description, String posterUrl, String guideBookUrl) {
        this.id = id;
        this.title = title;
        this.provider = provider;
        this.deadline = deadline;
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

    public String getProvider() {
        return provider;
    }

    public String getDescription() {
        return description;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getDeadlineRaw() {
        return deadlineRaw;
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
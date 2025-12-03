package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class Event {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("organizer")
    private String organizer;

    @SerializedName("category")
    private String category;

    @SerializedName("description")
    private String description;

    @SerializedName("date")
    private String date;

    @SerializedName("tgl_mulai")
    private String tglMulai;

    @SerializedName("tgl_selesai")
    private String tglSelesai;

    @SerializedName("waktu_mulai")
    private String waktuMulai;

    @SerializedName("waktu_selesai")
    private String waktuSelesai;

    @SerializedName("location")
    private String location;

    @SerializedName("posterUrl")
    private String posterUrl;

    @SerializedName("guideBookUrl")
    private String guideBookUrl;

    @SerializedName("guideBookFilename")
    private String guideBookFilename;

    // Constructor kosong
    public Event() {}

    // Constructor lengkap untuk backward compatibility dengan dummy data
    public Event(String id, String title, String organizer, String date, String time,
                 int participantCount, String posterUrl, String location,
                 String description, String guideBookUrl, String category) {
        this.id = id;
        this.title = title;
        this.organizer = organizer;
        this.date = date;
        this.location = location;
        this.posterUrl = posterUrl;
        this.description = description;
        this.guideBookUrl = guideBookUrl;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTglMulai() {
        return tglMulai;
    }

    public String getTglSelesai() {
        return tglSelesai;
    }

    // === TAMBAHAN BARU: GETTER WAKTU ===
    public String getWaktuMulai() {
        return waktuMulai;
    }

    public String getWaktuSelesai() {
        return waktuSelesai;
    }
    // ===================================

    public String getLocation() {
        return location;
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

    // Helper method
    public boolean hasGuideBook() {
        return guideBookUrl != null && !guideBookUrl.isEmpty();
    }

    public String getWaktuLengkap() {
        if (waktuMulai != null && waktuSelesai != null &&
                !waktuMulai.isEmpty() && !waktuSelesai.isEmpty()) {
            return waktuMulai + " - " + waktuSelesai;
        }
        return "-";
    }

    public boolean hasTimeInfo() {
        return waktuMulai != null && waktuSelesai != null &&
                !waktuMulai.isEmpty() && !waktuSelesai.isEmpty();
    }

    public String getTime() {
        return ""; // Return empty untuk compatibility
    }

    public int getParticipantCount() {
        return 0; // Return 0 untuk compatibility
    }

    public String getParticipantsText() {
        return "0 peserta";
    }
}
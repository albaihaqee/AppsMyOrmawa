package com.inovarka.myormawa.models;

public class Notification {

    private String id;
    private String title;
    private String message;
    private String category;
    private String time;
    private boolean isRead;
    private String createdAt;

    // ADD
    private String status = ""; // default kosong

    // Constructor umum (event / kompetisi / beasiswa)
    public Notification(String id, String title, String message,
                        String category, String createdAt, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.category = category;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    // Constructor untuk notifikasi pendaftaran (OPEN RECRUITMENT)
    public Notification(String id, String title, String message,
                        String category, String createdAt, boolean isRead,
                        String status) {

        this.id = id;
        this.title = title;
        this.message = message;
        this.category = category;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.status = status;
    }

    // Constructor khusus beasiswa (tanpa status)
    public static Notification fromScholarship(
            String id, String title, String provider,
            String description, String createdAt
    ) {

        Notification n = new Notification(
                id,
                title,
                description,
                "Beasiswa",
                createdAt,
                false
        );

        return n;
    }

    // Setter waktu (text "2 jam lalu")
    public void setTime(String time) {
        this.time = time;
    }

    // ============================
    //       GETTERS
    // ============================
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getCategory() { return category; }
    public String getTime() { return time; }
    public boolean isRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }
    public String getStatus() { return status; }   // ADD

    // ============================
    //       SETTERS
    // ============================
    public void setRead(boolean read) { isRead = read; }
    public void setStatus(String status) { this.status = status; } // ADD
}

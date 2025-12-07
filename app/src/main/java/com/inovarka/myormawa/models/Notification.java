package com.inovarka.myormawa.models;

public class Notification {
    private String id;
    private String title;
    private String message;
    private String category;
    private String time;
    private boolean isRead;
    private String createdAt;

    // Constructor umum (kompetisi)
    public Notification(String id, String title, String message,
                        String category, String createdAt, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.category = category;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    // Constructor khusus beasiswa
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

    public void setTime(String time) {
        this.time = time;
    }

    // Getter
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getCategory() { return category; }
    public String getTime() { return time; }
    public boolean isRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }

    // Setter
    public void setRead(boolean read) { isRead = read; }
}

package com.inovarka.myormawa.models;

public class Notification {
    private String id;
    private String title;
    private String message;
    private String category;
    private String time;
    private boolean isRead;

    public Notification(String id, String title, String message, String category, String time, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.category = category;
        this.time = time;
        this.isRead = isRead;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getCategory() { return category; }
    public String getTime() { return time; }
    public boolean isRead() { return isRead; }

    public void setRead(boolean read) { isRead = read; }
}
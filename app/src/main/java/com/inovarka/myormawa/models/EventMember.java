package com.inovarka.myormawa.models;

public class EventMember {
    private String id;
    private String title;
    private String category;
    private String location;
    private String date;
    private String startTime;
    private String endTime;
    private int participants;
    private String posterUrl;
    private String status; // "upcoming", "ongoing", "finished"
    private String description;

    public EventMember(String id, String title, String category, String location,
                 String date, String startTime, String endTime, int participants,
                 String posterUrl, String status, String description) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = participants;
        this.posterUrl = posterUrl;
        this.status = status;
        this.description = description;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public int getParticipants() { return participants; }
    public String getPosterUrl() { return posterUrl; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }

    public String getParticipantsText() {
        return participants + " peserta";
    }

    public String getTimeRange() {
        return startTime + " - " + endTime;
    }
}
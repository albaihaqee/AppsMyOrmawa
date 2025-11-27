package com.inovarka.myormawa.models;

public class Event {
    private String id;
    private String title;
    private String organizer;
    private String date;
    private String time;
    private int participantCount;
    private String posterUrl;
    private String location;
    private String description;
    private String guideBookUrl;
    private String category;

    public Event(String id, String title, String organizer, String date, String time,
                 int participantCount, String posterUrl, String location,
                 String description, String guideBookUrl, String category) {
        this.id = id;
        this.title = title;
        this.organizer = organizer;
        this.date = date;
        this.time = time;
        this.participantCount = participantCount;
        this.posterUrl = posterUrl;
        this.location = location;
        this.description = description;
        this.guideBookUrl = guideBookUrl;
        this.category = category;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getOrganizer() { return organizer; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getParticipantCount() { return participantCount; }
    public String getPosterUrl() { return posterUrl; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getGuideBookUrl() { return guideBookUrl; }
    public String getCategory() { return category; }

    public String getParticipantsText() {
        return participantCount + " peserta";
    }
}
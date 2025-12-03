package com.inovarka.myormawa.models;

public class NotificationMember {
    private String id;
    private String title;
    private String agenda;
    private String date;
    private String time;
    private String location;
    private String postedTime;

    public NotificationMember(String id, String title, String agenda, String date, String time, String location, String postedTime) {
        this.id = id;
        this.title = title;
        this.agenda = agenda;
        this.date = date;
        this.time = time;
        this.location = location;
        this.postedTime = postedTime;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAgenda() { return agenda; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getLocation() { return location; }
    public String getPostedTime() { return postedTime; }
}

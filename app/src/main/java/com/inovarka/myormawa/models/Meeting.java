package com.inovarka.myormawa.models;

public class Meeting {
    private String id;
    private String name;
    private String agenda;
    private String date;
    private String startTime;
    private String endTime;
    private String location;

    public Meeting(String id, String name, String agenda, String date, String startTime, String endTime, String location) {
        this.id = id;
        this.name = name;
        this.agenda = agenda;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAgenda() { return agenda; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getLocation() { return location; }

    public String getTimeRange() {
        return startTime + " - " + endTime;
    }
}
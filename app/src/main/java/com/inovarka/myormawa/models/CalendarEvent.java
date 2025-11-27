package com.inovarka.myormawa.models;

import java.util.Date;

public class CalendarEvent {
    private String id;
    private String title;
    private String location;
    private String time;
    private Date date;
    private String color;

    public CalendarEvent() {
    }

    public CalendarEvent(String id, String title, String location, String time, Date date, String color) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.time = time;
        this.date = date;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
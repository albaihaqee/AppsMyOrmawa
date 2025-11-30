package com.inovarka.myormawa.models;

public class ReminderItem {
    private Meeting meeting;
    private int minutesBefore; // 10, 30, 60

    public ReminderItem(Meeting meeting, int minutesBefore) {
        this.meeting = meeting;
        this.minutesBefore = minutesBefore;
    }

    public Meeting getMeeting() { return meeting; }
    public int getMinutesBefore() { return minutesBefore; }
}

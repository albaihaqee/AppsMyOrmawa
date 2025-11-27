package com.inovarka.myormawa.models;

import java.util.Calendar;

public class CalendarDate {
    private Calendar date;
    private boolean isCurrentMonth;
    private boolean isToday;
    private boolean isSelected;
    private boolean hasEvent;

    public CalendarDate(Calendar date, boolean isCurrentMonth) {
        this.date = date;
        this.isCurrentMonth = isCurrentMonth;
        this.isToday = false;
        this.isSelected = false;
        this.hasEvent = false;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public void setCurrentMonth(boolean currentMonth) {
        isCurrentMonth = currentMonth;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean hasEvent() {
        return hasEvent;
    }

    public void setHasEvent(boolean hasEvent) {
        this.hasEvent = hasEvent;
    }

    public int getDayOfMonth() {
        return date.get(Calendar.DAY_OF_MONTH);
    }
}
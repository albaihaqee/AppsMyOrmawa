package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class CalendarEvent {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("organizer")
    private String organizer;

    @SerializedName("location")
    private String location;

    @SerializedName("tgl_mulai")
    private String tglMulai;

    @SerializedName("tgl_selesai")
    private String tglSelesai;

    @SerializedName("waktu_mulai")
    private String waktuMulai;

    @SerializedName("waktu_selesai")
    private String waktuSelesai;

    @SerializedName("time_display")
    private String timeDisplay;

    @SerializedName("color")
    private String color;

    // Constructor kosong
    public CalendarEvent() {
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOrganizer() {
        return organizer;
    }

    public String getLocation() {
        return location;
    }

    public String getTglMulai() {
        return tglMulai;
    }

    public String getTglSelesai() {
        return tglSelesai;
    }

    public String getWaktuMulai() {
        return waktuMulai;
    }

    public String getWaktuSelesai() {
        return waktuSelesai;
    }

    public String getTimeDisplay() {
        return timeDisplay;
    }

    public String getColor() {
        return color;
    }

    // Helper method untuk compatibility dengan adapter
    public String getTime() {
        return timeDisplay;
    }
}
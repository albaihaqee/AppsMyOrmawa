package com.inovarka.myormawa.models;

public class AttendanceRequest {
    public String action;
    public String user_id;
    public String qr_code;
    public Double latitude;
    public Double longitude;

    // Constructor tanpa lokasi
    public AttendanceRequest(String action, String user_id, String qr_code) {
        this.action = action;
        this.user_id = user_id;
        this.qr_code = qr_code;
    }

    // Constructor dengan lokasi
    public AttendanceRequest(String action, String user_id, String qr_code, Double lat, Double lng) {
        this.action = action;
        this.user_id = user_id;
        this.qr_code = qr_code;
        this.latitude = lat;
        this.longitude = lng;
    }
}

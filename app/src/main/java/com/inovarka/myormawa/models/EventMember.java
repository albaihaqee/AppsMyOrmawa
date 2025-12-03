package com.inovarka.myormawa.models;

public class EventMember {

    private String id;
    private String title;
    private String location;
    private String description;
    private String tgl_mulai;
    private String tgl_selesai;
    private String waktu_mulai;
    private String waktu_selesai;
    private String posterUrl;
    private String ormawa_id;

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getTgl_mulai() { return tgl_mulai; }
    public String getTgl_selesai() { return tgl_selesai; }
    public String getWaktu_mulai() { return waktu_mulai; }
    public String getWaktu_selesai() { return waktu_selesai; }
    public String getPosterUrl() { return posterUrl; }
    public String getOrmawa_id() { return ormawa_id; }

    // Untuk tampilan
    public String getDisplayDate() {
        if (tgl_mulai == null || tgl_selesai == null) return "";
        if (tgl_mulai.equals(tgl_selesai)) return tgl_mulai;
        return tgl_mulai + " - " + tgl_selesai;
    }
}

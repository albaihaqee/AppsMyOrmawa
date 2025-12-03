package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Meeting {
    @SerializedName("id")
    private String id;

    @SerializedName("nama")  // pastikan sama dengan JSON API
    private String nama;

    @SerializedName("agenda")
    private String agenda;

    @SerializedName("tanggal")
    private String tanggal;

    @SerializedName("jam_mulai")
    private String jam_mulai;

    @SerializedName("jam_selesai")
    private String jam_selesai;

    @SerializedName("lokasi")
    private String lokasi;

    // getter
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getAgenda() { return agenda; }
    public String getTanggal() { return tanggal; }
    public String getJamMulai() { return jam_mulai; }
    public String getJamSelesai() { return jam_selesai; }
    public String getLokasi() { return lokasi; }

    public String getWaktu() {
        return jam_mulai + " - " + jam_selesai;
    }

    public long getTimeInMillis() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(this.tanggal + " " + this.jam_mulai);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }

}

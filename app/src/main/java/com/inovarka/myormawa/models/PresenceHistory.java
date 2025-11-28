package com.inovarka.myormawa.models;

public class PresenceHistory {

    private String id;
    private String title; // contoh: "Presensi Rapat Evaluasi Bulanan"
    private String date;
    private String startTime;     // jadwal mulai
    private String endTime;       // jadwal selesai
    private String userTime;      // waktu absen mahasiswa
    private String status;
    private String lateDetail;

    public PresenceHistory(String id, String title, String date, String startTime, String endTime, String userTime) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userTime = userTime;

        this.lateDetail = calculateLate(userTime, endTime);
        this.status = lateDetail.isEmpty() ? "Tepat Waktu" : "Terlambat";
    }

    private String calculateLate(String userTime, String endTime) {
        try {
            String[] user = userTime.split(":");
            String[] deadline = endTime.split(":");

            int userMinutes = Integer.parseInt(user[0]) * 60 + Integer.parseInt(user[1]);
            int endMinutes = Integer.parseInt(deadline[0]) * 60 + Integer.parseInt(deadline[1]);

            if (userMinutes <= endMinutes) return "";

            int diff = userMinutes - endMinutes;

            int hours = diff / 60;
            int minutes = diff % 60;

            if (hours > 0)
                return "Terlambat " + hours + " jam " + minutes + " menit";

            return "Terlambat " + minutes + " menit";

        } catch (Exception e) {
            return "";
        }
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getUserTime() { return userTime; }
    public String getStatus() { return status; }
    public String getLateDetail() { return lateDetail; }
}

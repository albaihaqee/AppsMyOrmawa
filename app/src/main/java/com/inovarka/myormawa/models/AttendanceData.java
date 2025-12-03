    package com.inovarka.myormawa.models;

    import com.google.gson.annotations.SerializedName;

    public class AttendanceData {
        @SerializedName("is_valid")
        public boolean is_valid;

        @SerializedName("event_id")
        public String event_id;

        @SerializedName("event_name")
        public String event_name;

        @SerializedName("event_date")
        public String event_date;

        @SerializedName("event_time")
        public String event_time;

        @SerializedName("location")
        public String location;

        @SerializedName("organization_name")
        public String organization_name;

        @SerializedName("status")
        public String status;

        @SerializedName("message")
        public String message;

        @SerializedName("location_required")
        public boolean location_required;

        @SerializedName("location_data")
        public LocationData location_data;

        @SerializedName("user_id")
        public String user_id;

        @SerializedName("id")
        public String id;

        @SerializedName("check_in_time")
        public String check_in_time;

        @SerializedName("tipe_absen")
        public String tipe_absen;

        @SerializedName("distance")
        public String distance;

        // Inner class untuk location data
        public static class LocationData {
            @SerializedName("lat")
            public double lat;

            @SerializedName("lng")
            public double lng;

            @SerializedName("radius")
            public double radius;
        }
    }
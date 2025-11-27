package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private ResponseData data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public ResponseData getData() { return data; }

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
}
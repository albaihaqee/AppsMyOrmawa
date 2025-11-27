package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private LoginData data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public LoginData getData() { return data; }

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
}
package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class ResendOtpRequest {
    @SerializedName("action")
    private String action;
    @SerializedName("email")
    private String email;

    @SerializedName("otp_type")
    private String otpType;

    @SerializedName("new_email")
    private String newEmail;

    public ResendOtpRequest(String action, String email, String otpType) {
        this.action = action;
        this.email = email;
        this.otpType = otpType;
    }

    public ResendOtpRequest(String action, String email, String otpType, String newEmail) {
        this.action = action;
        this.email = email;
        this.otpType = otpType;
        this.newEmail = newEmail;
    }

    public String getEmail() { return email; }
    public String getOtpType() { return otpType; }
    public String getNewEmail() { return newEmail; }
}
package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class VerifyOtpRequest {

    @SerializedName("action")
    private String action;
    @SerializedName("email")
    private String email;

    @SerializedName("otp_code")
    private String otpCode;

    @SerializedName("otp_type")
    private String otpType;

    public VerifyOtpRequest(String action, String email, String otpCode, String otpType) {
        this.action = action;
        this.email = email;
        this.otpCode = otpCode;
        this.otpType = otpType;
    }

    public String getEmail() { return email; }
    public String getOtpCode() { return otpCode; }
    public String getOtpType() { return otpType; }
}
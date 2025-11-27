package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class ResponseData {
    @SerializedName("email")
    private String email;

    @SerializedName("new_email")
    private String newEmail;

    @SerializedName("otp_for_testing")
    private String otpForTesting;

    @SerializedName("user")
    private User user;

    public String getEmail() { return email; }
    public String getNewEmail() { return newEmail; }
    public String getOtpForTesting() { return otpForTesting; }
    public User getUser() { return user; }
}
package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordRequest {
    @SerializedName("action")
    private String action;
    @SerializedName("email")
    private String email;

    public ForgotPasswordRequest(String action, String email) {
        this.action = action;
        this.email = email;
    }

    public String getEmail() { return email; }
}
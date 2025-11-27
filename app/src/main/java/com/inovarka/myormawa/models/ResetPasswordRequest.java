package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequest {
    @SerializedName("action")
    private String action;
    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public ResetPasswordRequest(String action, String email, String password) {
        this.action = action;
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {

    @SerializedName("action")
    private String action;
    @SerializedName("email")
    private String email;

    @SerializedName("old_password")
    private String oldPassword;

    @SerializedName("new_password")
    private String newPassword;

    public ChangePasswordRequest(String action, String email, String oldPassword, String newPassword) {
        this.action = action;
        this.email = email;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getEmail() { return email; }
    public String getOldPassword() { return oldPassword; }
    public String getNewPassword() { return newPassword; }
}
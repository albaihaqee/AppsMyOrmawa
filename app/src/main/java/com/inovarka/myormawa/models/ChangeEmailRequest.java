package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class ChangeEmailRequest {

    @SerializedName("action")
    private String action;
    @SerializedName("current_email")
    private String currentEmail;

    @SerializedName("new_email")
    private String newEmail;

    public ChangeEmailRequest(String action, String currentEmail, String newEmail) {
        this.action = action;
        this.currentEmail = currentEmail;
        this.newEmail = newEmail;
    }

    public String getCurrentEmail() { return currentEmail; }
    public String getNewEmail() { return newEmail; }
}
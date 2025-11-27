package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("action")
    private String action;
    @SerializedName("nim")
    private String nim;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("program_studi")
    private String programStudi;

    @SerializedName("password")
    private String password;

    public RegisterRequest(String action, String nim, String fullName, String email, String programStudi, String password) {
        this.action = action;
        this.nim = nim;
        this.fullName = fullName;
        this.email = email;
        this.programStudi = programStudi;
        this.password = password;
    }

    public String getNim() { return nim; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getProgramStudi() { return programStudi; }
    public String getPassword() { return password; }
}
package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;

    @SerializedName("nim")
    private String nim;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("program_studi")
    private String programStudi;

    @SerializedName("angkatan")
    private String angkatan;

    @SerializedName("level")
    private int level;

    @SerializedName("id_ormawa")
    private String idOrmawa;

    public int getId() { return id; }
    public String getNim() { return nim; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getProgramStudi() { return programStudi; }
    public String getAngkatan() { return angkatan; }
    public int getLevel() { return level; }
    public String getIdOrmawa() {
        return idOrmawa;
    }
}
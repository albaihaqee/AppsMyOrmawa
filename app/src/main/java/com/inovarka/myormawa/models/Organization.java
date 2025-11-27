package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Organization implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("nama_ormawa")
    private String name;

    @SerializedName("kategori")
    private String category;

    @SerializedName("deskripsi")
    private String description;

    @SerializedName("visi")
    private String vision;

    @SerializedName("misi")
    private String mission;

    @SerializedName("email")
    private String email;

    @SerializedName("contact_person")
    private String contactPerson;

    @SerializedName("logo_url")
    private String logoUrl;

    @SerializedName("created_at")
    private String createdAt;

    public Organization() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getVision() {
        return vision;
    }

    public String getMission() {
        return mission;
    }

    public String getEmail() {
        return email;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
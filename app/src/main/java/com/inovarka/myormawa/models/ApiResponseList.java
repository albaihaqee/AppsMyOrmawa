package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiResponseList<T> {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<T> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<T> getData() {
        return data;
    }
}
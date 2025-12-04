// FormSubmitRequest.java
package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FormSubmitRequest {
    @SerializedName("form_info_id")
    private String formInfoId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("submissions")
    private List<FormSubmission> submissions;

    public FormSubmitRequest(String formInfoId, String userId, List<FormSubmission> submissions) {
        this.formInfoId = formInfoId;
        this.userId = userId;
        this.submissions = submissions;
    }

    public String getFormInfoId() { return formInfoId; }
    public void setFormInfoId(String formInfoId) { this.formInfoId = formInfoId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<FormSubmission> getSubmissions() { return submissions; }
    public void setSubmissions(List<FormSubmission> submissions) {
        this.submissions = submissions;
    }
}
package com.inovarka.myormawa.models;

import com.google.gson.annotations.SerializedName;

public class FormSubmission {
    @SerializedName("field_id")
    private String fieldId;

    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("value")
    private String value;

    public FormSubmission(String fieldId, String fieldName, String value) {
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.value = value;
    }

    public String getFieldId() { return fieldId; }
    public void setFieldId(String fieldId) { this.fieldId = fieldId; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}

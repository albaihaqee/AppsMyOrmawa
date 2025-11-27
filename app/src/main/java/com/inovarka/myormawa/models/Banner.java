package com.inovarka.myormawa.models;

public class Banner {
    private int imageResource;
    private String imageUrl;
    private String title;
    private String actionUrl;

    // Constructor untuk drawable resource
    public Banner(int imageResource) {
        this.imageResource = imageResource;
        this.imageUrl = null;
    }

    // Constructor untuk URL
    public Banner(String imageUrl) {
        this.imageUrl = imageUrl;
        this.imageResource = 0;
    }

    // Constructor lengkap
    public Banner(int imageResource, String title, String actionUrl) {
        this.imageResource = imageResource;
        this.title = title;
        this.actionUrl = actionUrl;
        this.imageUrl = null;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public boolean isFromUrl() {
        return imageUrl != null && !imageUrl.isEmpty();
    }
}
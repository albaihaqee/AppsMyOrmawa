package com.inovarka.myormawa.models;

public class Scholarship {
    private String id;
    private String title;
    private String provider;
    private String amount;
    private String deadline;
    private double minGPA;
    private String posterUrl;
    private String description;
    private String guideBookUrl;

    public Scholarship(String id, String title, String provider, String amount,
                       String deadline, double minGPA, String posterUrl,
                       String description, String guideBookUrl) {
        this.id = id;
        this.title = title;
        this.provider = provider;
        this.amount = amount;
        this.deadline = deadline;
        this.minGPA = minGPA;
        this.posterUrl = posterUrl;
        this.description = description;
        this.guideBookUrl = guideBookUrl;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getProvider() { return provider; }
    public String getAmount() { return amount; }
    public String getDeadline() { return deadline; }
    public double getMinGPA() { return minGPA; }
    public String getPosterUrl() { return posterUrl; }
    public String getDescription() { return description; }
    public String getGuideBookUrl() { return guideBookUrl; }

    public String getMinGPAText() {
        return "IPK min " + minGPA;
    }
}
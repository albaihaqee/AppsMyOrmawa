package com.inovarka.myormawa.models;

public class Oprec {
    private String id;
    private String title;
    private String organization;
    private String deadline;
    private int currentParticipants;
    private int maxParticipants;
    private String posterUrl;
    private String type;
    private String description;
    private boolean isActive;

    public Oprec(String id, String title, String organization, String deadline,
                 int currentParticipants, int maxParticipants, String posterUrl,
                 String type, String description, boolean isActive) {
        this.id = id;
        this.title = title;
        this.organization = organization;
        this.deadline = deadline;
        this.currentParticipants = currentParticipants;
        this.maxParticipants = maxParticipants;
        this.posterUrl = posterUrl;
        this.type = type;
        this.description = description;
        this.isActive = isActive;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public String getDeadline() { return deadline; }
    public int getCurrentParticipants() { return currentParticipants; }
    public int getMaxParticipants() { return maxParticipants; }
    public String getPosterUrl() { return posterUrl; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public boolean isActive() { return isActive; }

    public String getParticipantsText() {
        return currentParticipants + "/" + maxParticipants + " pendaftar";
    }
}
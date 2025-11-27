package com.inovarka.myormawa.models;

public class Competition {
    private String id;
    private String title;
    private String organizer;
    private String registrationPeriod;
    private String deadline;
    private String prize;
    private int teamSize;
    private String posterUrl;
    private String description;
    private String guideBookUrl;

    public Competition(String id, String title, String organizer, String registrationPeriod,
                       String deadline, String prize, int teamSize, String posterUrl,
                       String description, String guideBookUrl) {
        this.id = id;
        this.title = title;
        this.organizer = organizer;
        this.registrationPeriod = registrationPeriod;
        this.deadline = deadline;
        this.prize = prize;
        this.teamSize = teamSize;
        this.posterUrl = posterUrl;
        this.description = description;
        this.guideBookUrl = guideBookUrl;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getOrganizer() { return organizer; }
    public String getRegistrationPeriod() { return registrationPeriod; }
    public String getDeadline() { return deadline; }
    public String getPrize() { return prize; }
    public int getTeamSize() { return teamSize; }
    public String getPosterUrl() { return posterUrl; }
    public String getDescription() { return description; }
    public String getGuideBookUrl() { return guideBookUrl; }

    public String getTeamSizeText() {
        return "Maks " + teamSize + " tim";
    }
}
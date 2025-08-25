package com.example.uvms.models;

public class Tender {
    private String id;
    private String title;
    private String deadline;
    private String budget;

    public Tender(String id, String title, String deadline, String budget) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.budget = budget;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDeadline() { return deadline; }
    public String getBudget() { return budget; }
}
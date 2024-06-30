package com.example.eventplanning;

public class Event {
    private String id;
    private String name;
    private String location;
    private String date;
    private String time;

    // Default constructor required for Firestore
    public Event() {}

    // Parameterized constructor
    public Event(String name, String location, String date, String time) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.time = time;
    }

    // Getters and Setters for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getters and Setters for other fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

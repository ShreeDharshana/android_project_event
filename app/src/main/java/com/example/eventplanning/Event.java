package com.example.eventplanning;

public class Event {
    private String id; // Add this field for Firestore document ID
    private String name;
    private String location;
    private String dateTime;

    // Default constructor required for Firestore
    public Event() {}

    // Parameterized constructor
    public Event(String name, String location, String dateTime) {
        this.name = name;
        this.location = location;
        this.dateTime = dateTime;
    }

    // Getter and Setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDateTime() {
        return dateTime;
    }

    // Setter methods
    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}

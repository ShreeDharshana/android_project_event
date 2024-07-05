package com.example.eventplanning;

public class Event {
    private String id;
    private String name;
    private String location;
    private String date;
    private String time;
    private String image;
    private long timestamp;

    public Event() {
        // Default constructor required for Firestore
    }

    public Event(String name, String location, String date, String time, String image, long timestamp) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.time = time;
        this.image = image;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

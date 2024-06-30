package com.example.eventplanning;

public class Invitation {
    private String inviteeEmail;
    private String eventId;
    private boolean rsvpStatus;

    // Constructors, getters, setters
    public Invitation(String inviteeEmail, String eventId) {
        this.inviteeEmail = inviteeEmail;
        this.eventId = eventId;
        this.rsvpStatus = false; // Default RSVP status
    }

    public String getInviteeEmail() {
        return inviteeEmail;
    }

    public void setInviteeEmail(String inviteeEmail) {
        this.inviteeEmail = inviteeEmail;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public boolean isRsvpStatus() {
        return rsvpStatus;
    }

    public void setRsvpStatus(boolean rsvpStatus) {
        this.rsvpStatus = rsvpStatus;
    }
}

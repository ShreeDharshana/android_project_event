package com.example.eventplanning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EventDetailsActivity extends AppCompatActivity {

    private TextView textEventName, textEventLocation, textEventDate, textEventTime;
    private ImageView imageEvent;
    private Button buttonEditEvent, buttonDeleteEvent, button_attend, button_maybe, button_decline;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Initialize views
        textEventName = findViewById(R.id.text_event_name);
        textEventLocation = findViewById(R.id.text_event_location);
        textEventDate = findViewById(R.id.text_event_date);
        textEventTime = findViewById(R.id.text_event_time);
        imageEvent = findViewById(R.id.image_event);
        buttonEditEvent = findViewById(R.id.button_edit_event);
        buttonDeleteEvent = findViewById(R.id.button_delete_event);
        button_attend = findViewById(R.id.button_attend);
        button_maybe = findViewById(R.id.button_maybe);
        button_decline = findViewById(R.id.button_decline);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get event ID from Intent
        eventId = getIntent().getStringExtra("EVENT_ID");

        // Load event data
        loadEventData();

        // Set up button listeners
        buttonEditEvent.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsActivity.this, EditEventActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            startActivity(intent);
        });

        buttonDeleteEvent.setOnClickListener(v -> deleteEvent());

        button_attend.setOnClickListener(v -> respondToEvent("Yes"));
        button_decline.setOnClickListener(v -> respondToEvent("No"));
        button_maybe.setOnClickListener(v -> respondToEvent("Maybe"));
    }

    private void loadEventData() {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String eventName = documentSnapshot.getString("name");
                        String eventLocation = documentSnapshot.getString("location");
                        String eventDate = documentSnapshot.getString("date");
                        String eventTime = documentSnapshot.getString("time");
                        String eventImage = documentSnapshot.getString("image");

                        textEventName.setText(eventName);
                        textEventLocation.setText(eventLocation);
                        textEventDate.setText(eventDate);
                        textEventTime.setText(eventTime);

                        // Load event image using Glide (example code)
                        Glide.with(this)
                                .load(eventImage)
                                .placeholder(R.drawable.edit_event_bg) // Replace with your placeholder
                                .into(imageEvent);
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load event", Toast.LENGTH_SHORT).show());
    }

    private void respondToEvent(String response) {
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to RSVP", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference rsvpRef = db.collection("events").document(eventId).collection("rsvps").document(userId);

        rsvpRef.set(new RSVP(response))
                .addOnSuccessListener(aVoid -> Toast.makeText(EventDetailsActivity.this, "RSVP submitted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(EventDetailsActivity.this, "Failed to submit RSVP", Toast.LENGTH_SHORT).show());
    }

    private void deleteEvent() {
        db.collection("events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EventDetailsActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EventDetailsActivity.this, "Failed to delete event", Toast.LENGTH_SHORT).show());
    }

    private class RSVP {
        public RSVP(String response) {
        }
    }
}

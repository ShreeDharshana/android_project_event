package com.example.eventplanning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditEventActivity extends AppCompatActivity {

    private EditText eventNameEditText, eventLocationEditText, eventDateEditText, eventTimeEditText;
    private ImageView eventImageView;
    private Button saveEventButton;
    private String eventId;
    private long eventTimestamp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // Initialize views
        eventNameEditText = findViewById(R.id.edit_event_name);
        eventLocationEditText = findViewById(R.id.edit_event_location);
        eventDateEditText = findViewById(R.id.edit_event_date);
        eventTimeEditText = findViewById(R.id.edit_event_time);
        eventImageView = findViewById(R.id.edit_event_image);
        saveEventButton = findViewById(R.id.save_event_button);

        // Get event ID from intent
        Intent intent = getIntent();
        eventId = intent.getStringExtra("EVENT_ID");

        // Load event data
        loadEventData();

        saveEventButton.setOnClickListener(v -> saveEvent());
    }

    private void loadEventData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        if (event != null) {
                            eventNameEditText.setText(event.getName());
                            eventLocationEditText.setText(event.getLocation());
                            eventDateEditText.setText(event.getDate());
                            eventTimeEditText.setText(event.getTime());
                            eventTimestamp = event.getTimestamp(); // Get the current timestamp

                            if (event.getImage() != null && !event.getImage().isEmpty()) {
                                Glide.with(this).load(event.getImage()).into(eventImageView);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private void saveEvent() {
        String name = eventNameEditText.getText().toString();
        String location = eventLocationEditText.getText().toString();
        String date = eventDateEditText.getText().toString();
        String time = eventTimeEditText.getText().toString();

        Event updatedEvent = new Event(name, location, date, time, null, eventTimestamp);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).set(updatedEvent)
                .addOnSuccessListener(aVoid -> {
                    // Handle successful update
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}

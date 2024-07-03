package com.example.eventplanning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailsActivity extends AppCompatActivity {

    private TextView textEventName, textEventLocation, textEventDate, textEventTime;
    private ImageView imageEvent;
    private Button buttonEditEvent, buttonDeleteEvent;
    private FirebaseFirestore db;
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

        db = FirebaseFirestore.getInstance();

        // Get event ID from Intent
        eventId = getIntent().getStringExtra("EVENT_ID");

        // Load event data
        loadEventData();

        // Set up button listeners
        buttonEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailsActivity.this, EditEventActivity.class);
                intent.putExtra("EVENT_ID", eventId);
                startActivity(intent);
            }
        });

        buttonDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEvent();
            }
        });
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

                        // Load event image using your preferred image loading library (e.g., Glide or Picasso)
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load event", Toast.LENGTH_SHORT).show());
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
}

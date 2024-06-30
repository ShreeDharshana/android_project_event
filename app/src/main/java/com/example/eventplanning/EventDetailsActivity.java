package com.example.eventplanning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {

    private TextView textEventName;
    private TextView textEventLocation;
    private TextView textEventDateTime;
    private Button buttonEditEvent;
    private Button buttonDeleteEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Initialize views
        textEventName = findViewById(R.id.text_event_name);
        textEventLocation = findViewById(R.id.text_event_location);
        textEventDateTime = findViewById(R.id.text_event_date_time);
        buttonEditEvent = findViewById(R.id.button_edit_event);
        buttonDeleteEvent = findViewById(R.id.button_delete_event);

        // Retrieve event details from intent
        Intent intent = getIntent();
        String eventName = intent.getStringExtra("eventName");
        String eventLocation = intent.getStringExtra("eventLocation");
        String eventDateTime = intent.getStringExtra("eventDateTime");

        // Set event details to TextViews
        textEventName.setText(eventName);
        textEventLocation.setText(eventLocation);
        textEventDateTime.setText(eventDateTime);

        // Edit event button click listener
        buttonEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement edit event functionality
                // Example: Launch EditEventActivity or fragment
                // Intent editIntent = new Intent(EventDetailActivity.this, EditEventActivity.class);
                // editIntent.putExtra("eventName", eventName);
                // editIntent.putExtra("eventLocation", eventLocation);
                // editIntent.putExtra("eventDateTime", eventDateTime);
                // startActivity(editIntent);
            }
        });

        // Delete event button click listener
        buttonDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement delete event functionality
                // Example: Show confirmation dialog and delete event
            }
        });
    }
}

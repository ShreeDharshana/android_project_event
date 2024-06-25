package com.example.eventplanning;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {

    private TextView textViewEventName, textViewEventLocation, textViewEventDateTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        textViewEventName = findViewById(R.id.text_view_event_name);
        textViewEventLocation = findViewById(R.id.text_view_event_location);
        textViewEventDateTime = findViewById(R.id.text_view_event_date_time);

        String eventName = getIntent().getStringExtra("eventName");
        String eventLocation = getIntent().getStringExtra("eventLocation");
        String eventDateTime = getIntent().getStringExtra("eventDateTime");

        textViewEventName.setText(eventName);
        textViewEventLocation.setText(eventLocation);
        textViewEventDateTime.setText(eventDateTime);
    }
}

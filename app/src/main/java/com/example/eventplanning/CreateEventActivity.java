package com.example.eventplanning;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    private EditText editTextEventName, editTextEventLocation, editTextEventDate, editTextEventTime;
    private Button buttonCreateEvent;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        editTextEventName = findViewById(R.id.edit_text_event_name);
        editTextEventLocation = findViewById(R.id.edit_text_event_location);
        editTextEventDate = findViewById(R.id.edit_text_event_date);
        editTextEventTime = findViewById(R.id.edit_text_event_time);
        buttonCreateEvent = findViewById(R.id.btn_create_event);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEventDate.setOnClickListener(v -> showDatePickerDialog());
        editTextEventTime.setOnClickListener(v -> showTimePickerDialog());

        buttonCreateEvent.setOnClickListener(v -> createEvent());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%d", dayOfMonth, (monthOfYear + 1), year1);
                    editTextEventDate.setText(date);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minute1);
                    editTextEventTime.setText(time);
                },
                hour, minute, true);
        timePickerDialog.show();
    }

    private void createEvent() {
        String eventName = editTextEventName.getText().toString().trim();
        String eventLocation = editTextEventLocation.getText().toString().trim();
        String eventDate = editTextEventDate.getText().toString().trim();
        String eventTime = editTextEventTime.getText().toString().trim();

        if (TextUtils.isEmpty(eventName)) {
            Toast.makeText(this, "Please enter the event name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(eventLocation)) {
            Toast.makeText(this, "Please enter the event location", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(eventDate)) {
            Toast.makeText(this, "Please select the event date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(eventTime)) {
            Toast.makeText(this, "Please select the event time", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> event = new HashMap<>();
        event.put("name", eventName);
        event.put("location", eventLocation);
        event.put("date", eventDate);
        event.put("time", eventTime);
        event.put("userId", userId);

        db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(CreateEventActivity.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateEventActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateEventActivity.this, "Error creating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

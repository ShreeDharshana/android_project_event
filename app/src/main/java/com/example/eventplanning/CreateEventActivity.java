package com.example.eventplanning;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText editTextEventName, editTextEventLocation, editTextEventDate, editTextEventTime;
    private Button buttonCreateEvent, buttonUploadImage;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        editTextEventName = findViewById(R.id.edit_text_event_name);
        editTextEventLocation = findViewById(R.id.edit_text_event_location);
        editTextEventDate = findViewById(R.id.edit_text_event_date);
        editTextEventTime = findViewById(R.id.edit_text_event_time);
        buttonCreateEvent = findViewById(R.id.btn_create_event);
        buttonUploadImage = findViewById(R.id.btn_upload_image);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEventDate.setOnClickListener(v -> showDatePickerDialog());
        editTextEventTime.setOnClickListener(v -> showTimePickerDialog());

        buttonCreateEvent.setOnClickListener(v -> createEvent());
        buttonUploadImage.setOnClickListener(v -> openFileChooser());
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
            editTextEventName.setError("Please enter the event name");
            return;
        }

        if (TextUtils.isEmpty(eventLocation)) {
            editTextEventLocation.setError("Please enter the event location");
            return;
        }

        if (TextUtils.isEmpty(eventDate)) {
            editTextEventDate.setError("Please select the event date");
            return;
        }

        if (TextUtils.isEmpty(eventTime)) {
            editTextEventTime.setError("Please select the event time");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> event = new HashMap<>();
        event.put("name", eventName);
        event.put("location", eventLocation);
        event.put("date", eventDate);
        event.put("time", eventTime);
        event.put("userId", userId);
        event.put("timestamp", System.currentTimeMillis()); // Add timestamp

        db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId(); // Get the ID of the created event
                    Toast.makeText(CreateEventActivity.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                    scheduleNotification(eventName, eventDate, eventTime);

                    // Remove or comment out the following lines
                    // Pass eventId to SendInvitationActivity
                    // Intent intent = new Intent(CreateEventActivity.this, SendInvitationActivity.class);
                    // intent.putExtra("EVENT_ID", eventId);
                    // startActivity(intent);
                    // finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateEventActivity.this, "Error creating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Handle the image upload here if needed
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(String eventName, String eventDate, String eventTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(eventDate + " " + eventTime));
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing date/time.", Toast.LENGTH_SHORT).show();
            return; // Exit if parsing fails
        }

        long eventMillis = calendar.getTimeInMillis();
        long reminderMillis = eventMillis - 2 * 60 * 1000; // 2 minutes before the event

        // Log the scheduled time
        Log.d("CreateEventActivity", "Event scheduled at: " + calendar.getTime());
        Log.d("CreateEventActivity", "Reminder scheduled at: " + new Date(reminderMillis));

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("EVENT_NAME", eventName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderMillis, pendingIntent);
            } else {
                requestExactAlarmPermission();
            }
        } else {
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, reminderMillis, pendingIntent);
            } catch (SecurityException e) {
                Log.e("CreateEventActivity", "SecurityException: " + e.getMessage());
                Toast.makeText(this, "Permission required to set exact alarms.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean canScheduleExactAlarms() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Method method = AlarmManager.class.getMethod("canScheduleExactAlarms");
                return (boolean) method.invoke(alarmManager);
            }
        } catch (Exception e) {
            Log.e("CreateEventActivity", "Exception in canScheduleExactAlarms: " + e.getMessage());
        }
        return false;
    }

    private void requestExactAlarmPermission() {
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        startActivity(intent);
    }
}

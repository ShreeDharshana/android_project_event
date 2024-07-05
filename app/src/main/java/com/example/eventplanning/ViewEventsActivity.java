package com.example.eventplanning;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ViewEventsActivity extends AppCompatActivity {

    private LinearLayout eventsContainer;
    private TextView noEventsTextView;
    private FirebaseFirestore db;
    private Animation scaleDown;
    private Animation scaleUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        eventsContainer = findViewById(R.id.events_container);
        noEventsTextView = findViewById(R.id.no_events_text_view);
        db = FirebaseFirestore.getInstance();

        // Load animations
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);

        fetchEvents();
    }

    private void fetchEvents() {
        db.collection("events")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        eventsContainer.removeAllViews();
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            noEventsTextView.setVisibility(View.GONE);
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                Event event = document.toObject(Event.class);
                                if (event != null) {
                                    event.setId(document.getId());
                                    addEventView(event);
                                }
                            }
                        } else {
                            noEventsTextView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void addEventView(final Event event) {
        View eventView = LayoutInflater.from(this).inflate(R.layout.event_item, eventsContainer, false);

        TextView eventName = eventView.findViewById(R.id.event_name);
        TextView eventLocation = eventView.findViewById(R.id.event_location);
        TextView eventDate = eventView.findViewById(R.id.event_date);
        TextView eventTime = eventView.findViewById(R.id.event_time);
        ImageView eventImage = eventView.findViewById(R.id.event_image);
        Button editButton = eventView.findViewById(R.id.edit_event_button);
        Button deleteButton = eventView.findViewById(R.id.delete_event_button);

        eventName.setText(event.getName());
        eventLocation.setText(event.getLocation());
        eventDate.setText(event.getDate());
        eventTime.setText(event.getTime());

        if (event.getImage() != null && !event.getImage().isEmpty()) {
            Glide.with(this).load(event.getImage()).into(eventImage);
        }

        editButton.setOnClickListener(v -> {
            // Apply scale down animation on click
            v.startAnimation(scaleDown);
            v.postDelayed(() -> {
                Intent intent = new Intent(ViewEventsActivity.this, EditEventActivity.class);
                intent.putExtra("EVENT_ID", event.getId());
                startActivity(intent);
            }, scaleDown.getDuration());
        });

        deleteButton.setOnClickListener(v -> {
            // Apply scale down animation on click
            v.startAnimation(scaleDown);
            v.postDelayed(() -> {
                db.collection("events").document(event.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ViewEventsActivity.this, "Event deleted", Toast.LENGTH_SHORT).show();
                            fetchEvents(); // Refresh the events list after deletion
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ViewEventsActivity.this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                        });
            }, scaleDown.getDuration());
        });

        // Apply slide-in animation
        eventView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in));

        eventsContainer.addView(eventView);
    }
}

package com.example.eventplanning;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewEventsActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        // Initialize RecyclerView and layout manager
        recyclerView = findViewById(R.id.recycler_view_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize event list and adapter
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList, this);
        recyclerView.setAdapter(eventAdapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch events from Firestore
        fetchEvents();
    }

    private void fetchEvents() {
        db.collection("events")
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("ViewEventsActivity", "Listen failed.", e);
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            eventList.clear();
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                Event event = document.toObject(Event.class);
                                event.setId(document.getId());
                                eventList.add(event);
                            }
                            eventAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onDeleteClick(Event event) {
        // Handle delete click
        db.collection("events").document(event.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ViewEventsActivity.this, "Event deleted", Toast.LENGTH_SHORT).show();
                    fetchEvents(); // Refresh the events list
                })
                .addOnFailureListener(e -> Toast.makeText(ViewEventsActivity.this, "Failed to delete event", Toast.LENGTH_SHORT).show());
    }
}
